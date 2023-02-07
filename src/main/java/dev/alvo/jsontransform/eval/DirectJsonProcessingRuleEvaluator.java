package dev.alvo.jsontransform.eval;

import dev.alvo.jsontransform.JsonProcessingRule;

public interface DirectJsonProcessingRuleEvaluator<T> extends JsonProcessingRuleEvaluator<T, T> {
  T evaluate(final T source, final JsonProcessingRule rule);
}

