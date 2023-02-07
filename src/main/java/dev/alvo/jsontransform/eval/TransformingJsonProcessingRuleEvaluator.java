package dev.alvo.jsontransform.eval;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import dev.alvo.jsontransform.JsonProcessingRule;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class TransformingJsonProcessingRuleEvaluator implements
  DirectJsonProcessingRuleEvaluator<String> {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(TransformingJsonProcessingRuleEvaluator.class);

  private final Configuration jsonPathConfiguration =
    Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);

  @Override
  public String evaluate(String json, JsonProcessingRule rule) {
    if (!isJsonValid(json)) {
      LOGGER.warn("Can't apply processing rule: {} to the json string: {}", rule, json);
      return json;
    }

    // We need to create a synthetic root node to properly map over document internals.
    // For some reason jayway JsonPath can't properly map over the simple root path("$")
    // For details see com.jayway.jsonpath.internal.RootPathRef#convert(mapFunction, config)
    var content = "{ \"root\":" + json + "}";

    switch (rule.getOperation()) {
      case REPLACE_REGEX:
        content = applyReplaceRegexRule(content, rule);
        break;
      case COPY:
        content = applyCopyRule(content, rule);
        break;
      case DELETE:
        content = applyDeleteRule(content, rule);
        break;
      case REPLACE:
        content = applyReplaceRule(content, rule);
        break;
      case ADD:
        content = applyAddRule(content, rule);
        break;
      case MOVE:
        content = applyMoveRule(content, rule);
        break;
      default:
        throw new IllegalStateException("Unexpected operation: " + rule.getOperation());
    }

    // In the end we "un-wrap" the processed content to get rid of the synthetic root node
    return JsonPath.parse((Object) JsonPath.parse(content).read("$.root")).jsonString();
  }

  private static boolean isJsonValid(final String json) {
    return StringUtils.isNotBlank(json) && JsonPath.parse(json).json() instanceof Map;
  }

  private String applyAddRule(final String json, final JsonProcessingRule rule) {
    try {
      return JsonPath.parse(json, jsonPathConfiguration)
        .add(rule.getPath(), rule.getValue())
        .jsonString();
    } catch (Exception ex) {
      LOGGER.error("Error during evaluation of the add rule: {}", ex.getMessage());
      return json;
    }
  }

  private String applyReplaceRule(final String json, final JsonProcessingRule rule) {
    try {
      return JsonPath.parse(json, jsonPathConfiguration)
        .delete(rule.getPath())
        .add(rule.getPath(), rule.getValue())
        .jsonString();
    } catch (Exception ex) {
      LOGGER.error("Error during evaluation of the replace rule: {}", ex.getMessage());
      return json;
    }
  }

  private String applyDeleteRule(final String json, final JsonProcessingRule rule) {
    try {
      return JsonPath.parse(json, jsonPathConfiguration)
        .delete(rule.getPath())
        .jsonString();
    } catch (Exception ex) {
      LOGGER.error("Error during evaluation of the delete rule: {}", ex.getMessage());
      return json;
    }
  }

  private String applyMoveRule(final String json, final JsonProcessingRule rule) {
    try {
      return JsonPath.parse(json, jsonPathConfiguration)
        .map(rule.getPath(), (object, __) -> {
          try {
            final var subDocument = JsonPath.parse(object);
            subDocument.delete(rule.getFrom());
            return subDocument.add(rule.getTo(), rule.getValue()).json();
          } catch (Exception ex) {
            LOGGER.error("Error during evaluation of the move rule: {}", ex.getMessage());
            return object;
          }
        }).jsonString();
    } catch (Exception ex) {
      LOGGER.error("Error during evaluation of the move rule: {}", ex.getMessage());
      return json;
    }
  }

  private String applyReplaceRegexRule(final String json, final JsonProcessingRule rule) {
    try {
      return JsonPath.parse(json, jsonPathConfiguration)
        .map(
          rule.getPath(),
          (object, __) -> {
            try {
              final var replaced = JsonPath.parse(object)
                .jsonString()
                .replaceAll(rule.getFrom(), rule.getTo());

              return JsonPath.parse(replaced).json();
            } catch (Exception ex) {
              LOGGER.error("Error during evaluation of the replace regex rule: {}", ex.getMessage());
              return object;
            }
          })
        .jsonString();
    } catch (Exception ex) {
      LOGGER.error("Error during evaluation of the replace regex rule: {}", ex.getMessage());
      return json;
    }
  }

  private String applyCopyRule(final String json, final JsonProcessingRule rule) {
    try {
      return JsonPath.parse(json, jsonPathConfiguration)
        .map(
          rule.getPath(),
          (object, __) -> {
            try {
              final var parsed = JsonPath.parse(object);
              final var nodeToCopy = parsed.read(rule.getFrom());
              final var segments = rule.getTo().split("\\.");
              final var key = segments[segments.length - 1];
              final var path = Arrays.stream(segments)
                .limit(segments.length - 1)
                .collect(Collectors.joining("."));

              return parsed.put(path, key, nodeToCopy).json();
            } catch (Exception ex) {
              LOGGER.error("Error during evaluation of the copy rule: {}", ex.getMessage());
              return object;
            }
          })
        .jsonString();
    } catch (Exception ex) {
      LOGGER.error("Error during evaluation of the copy rule: {}", ex.getMessage());
      return json;
    }
  }
}
