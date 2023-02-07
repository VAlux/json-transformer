package dev.alvo.jsontransform.validation;

import dev.alvo.jsontransform.JsonProcessingOperation;
import dev.alvo.jsontransform.JsonProcessingRule;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Optional;

import static dev.alvo.jsontransform.JsonProcessingOperation.ADD;
import static dev.alvo.jsontransform.JsonProcessingOperation.REPLACE;

public class ValueIsNotBlankRuleValidator implements JsonProcessingRuleValidator {

  private static final EnumSet<JsonProcessingOperation> supportedOperations
    = EnumSet.of(REPLACE, ADD);

  @Override
  public Optional<String> validate(JsonProcessingRule rule) {
    if (StringUtils.isBlank(rule.getValue())) {
      return Optional.of(formulateEmptyValueError(rule));
    }

    return Optional.empty();
  }

  public static String formulateEmptyValueError(final JsonProcessingRule rule) {
    return "[value] clause is null or empty for rule " + rule.toString();
  }

  @Override
  public boolean isApplicableFor(JsonProcessingRule rule) {
    return supportedOperations.contains(rule.getOperation());
  }
}
