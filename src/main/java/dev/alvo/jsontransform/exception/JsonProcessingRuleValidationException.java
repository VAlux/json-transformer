package dev.alvo.jsontransform.exception;

import java.util.List;
import java.util.stream.Collectors;

public class JsonProcessingRuleValidationException extends RuntimeException {
  public JsonProcessingRuleValidationException(final List<String> errors) {
    super("Validation failure for json processing rules: " +
      errors.stream().collect(Collectors.joining(System.lineSeparator())));
  }
}
