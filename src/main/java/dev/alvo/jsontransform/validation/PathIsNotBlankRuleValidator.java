package dev.alvo.jsontransform.validation;

import com.jayway.jsonpath.JsonPath;
import dev.alvo.jsontransform.JsonProcessingRule;

import java.util.Optional;

import static dev.alvo.utils.Utils.getOrNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class PathIsNotBlankRuleValidator implements JsonProcessingRuleValidator {

  @Override
  public Optional<String> validate(JsonProcessingRule rule) {
    if (isBlank(getOrNull(rule, JsonProcessingRule::getPath, JsonPath::getPath))) {
      return Optional.of(formulateEmptyPathError(rule));
    }

    return Optional.empty();
  }

  public static String formulateEmptyPathError(final JsonProcessingRule rule) {
    return "[path] clause is null or empty for rule: " + rule.toString();
  }

  @Override
  public boolean isApplicableFor(JsonProcessingRule rule) {
    return true; // is applicable for any rule
  }

}
