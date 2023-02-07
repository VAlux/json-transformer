package dev.alvo.jsontransform.validation;

import dev.alvo.jsontransform.JsonProcessingRule;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonProcessingRuleValidationEvaluator {

  private final List<JsonProcessingRuleValidator> validators;

  public JsonProcessingRuleValidationEvaluator(List<JsonProcessingRuleValidator> validators) {
    this.validators = validators;
  }

  public List<String> runValidation(final List<JsonProcessingRule> rules) {
    return rules.stream()
      .map(rule -> new ProcessingRuleValidationMapping(rule, getApplicableValidators(rule)))
      .flatMap(ProcessingRuleValidationMapping::runValidation)
      .collect(Collectors.toList());
  }

  private List<JsonProcessingRuleValidator> getApplicableValidators(final JsonProcessingRule rule) {
    return validators.stream()
      .filter(validator -> validator.isApplicableFor(rule))
      .collect(Collectors.toList());
  }

  private static final class ProcessingRuleValidationMapping {

    private final JsonProcessingRule rule;
    private final List<JsonProcessingRuleValidator> validators;

    private ProcessingRuleValidationMapping(JsonProcessingRule rule,
                                            List<JsonProcessingRuleValidator> validators) {
      this.rule = rule;
      this.validators = validators;
    }

    public Stream<String> runValidation() {
      return validators.stream().flatMap(validator -> validator.validate(rule).stream());
    }
  }
}
