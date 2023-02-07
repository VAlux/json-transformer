package dev.alvo.jsontransform.validation;

import dev.alvo.jsontransform.JsonProcessingOperation;
import dev.alvo.jsontransform.JsonProcessingRule;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Optional;

import static dev.alvo.jsontransform.JsonProcessingOperation.COPY;
import static dev.alvo.jsontransform.JsonProcessingOperation.MOVE;
import static dev.alvo.jsontransform.JsonProcessingOperation.REPLACE_REGEX;

public class LocationsNotBlankRuleValidator implements JsonProcessingRuleValidator {

  private static final EnumSet<JsonProcessingOperation> supportedOperations
    = EnumSet.of(REPLACE_REGEX, COPY, MOVE);

  @Override
  public Optional<String> validate(JsonProcessingRule rule) {
    if (StringUtils.isBlank(rule.getFrom())) {
      return Optional.of(formulateEmptyFromError(rule));
    }

    if (StringUtils.isBlank(rule.getTo())) {
      return Optional.of(formulateEmptyToError(rule));
    }

    return Optional.empty();
  }

  public static String formulateEmptyFromError(final JsonProcessingRule rule) {
    return "[from] clause is null or empty for rule: " + rule.toString();
  }

  public static String formulateEmptyToError(final JsonProcessingRule rule) {
    return "[to] clause is null or empty for rule: " + rule.toString();
  }

  @Override
  public boolean isApplicableFor(JsonProcessingRule rule) {
    return supportedOperations.contains(rule.getOperation());
  }
}
