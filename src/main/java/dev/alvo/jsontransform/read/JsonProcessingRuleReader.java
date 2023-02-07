package dev.alvo.jsontransform.read;

import dev.alvo.jsontransform.JsonProcessingRule;

import java.util.List;
import java.util.Optional;

public interface JsonProcessingRuleReader<T> {
  Optional<List<JsonProcessingRule>> read(final T source);
}
