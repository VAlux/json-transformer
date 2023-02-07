package dev.alvo.jsontransform.validation;

import dev.alvo.jsontransform.JsonProcessingRule;

import java.util.Optional;

public interface JsonProcessingRuleValidator {
  Optional<String> validate(final JsonProcessingRule rule);

  boolean isApplicableFor(final JsonProcessingRule rule);
}
