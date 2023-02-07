package dev.alvo.jsontransform.eval;

import dev.alvo.jsontransform.JsonProcessingRule;

public interface JsonProcessingRuleEvaluator<T, R> {
  R evaluate(final T source, final JsonProcessingRule rule);
}

