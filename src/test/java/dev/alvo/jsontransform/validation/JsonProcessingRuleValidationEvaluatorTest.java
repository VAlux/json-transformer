package dev.alvo.jsontransform.validation;

import com.jayway.jsonpath.JsonPath;
import dev.alvo.jsontransform.JsonProcessingRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.alvo.jsontransform.JsonProcessingOperation.REPLACE;
import static dev.alvo.jsontransform.JsonProcessingOperation.REPLACE_REGEX;

class JsonProcessingRuleValidationEvaluatorTest {

  private JsonProcessingRuleValidationEvaluator evaluator;

  @BeforeEach
  void setUp() {
    this.evaluator = new JsonProcessingRuleValidationEvaluator(
      List.of(
        new LocationsNotBlankRuleValidator(),
        new PathIsNotBlankRuleValidator(),
        new ValueIsNotBlankRuleValidator()
      )
    );
  }

  @ParameterizedTest
  @MethodSource("supplyRules")
  void testValidationFlow(List<JsonProcessingRule> rules, List<String> expected) {
    final var actual = evaluator.runValidation(rules);

    final var actualErrorsSummary =
      actual.stream().collect(Collectors.joining(System.lineSeparator()));

    System.out.println("Actual validation errors are: " + actualErrorsSummary);

    Assertions.assertEquals(expected, actual);
  }

  public static Stream<Arguments> supplyRules() {
    final var replaceRegexEmptyToRule =
      new JsonProcessingRule(REPLACE_REGEX, "$", "test", null);

    final var replaceRegexEmptyFromRule =
      new JsonProcessingRule(REPLACE_REGEX, "$", null, "test");

    final var replaceRegexEmptyPathRule =
      new JsonProcessingRule(REPLACE_REGEX, (JsonPath) null, "test", "test_2");

    final var replaceEmptyValueRule =
      new JsonProcessingRule(REPLACE, "$", "{\"test\": 10}", "{\"test\": 20}");

    final var replaceRegexCorrectRule =
      new JsonProcessingRule(REPLACE_REGEX, "$", "test", "test_2");

    return Stream.of(
      Arguments.of(
        List.of(replaceRegexEmptyToRule),
        List.of(LocationsNotBlankRuleValidator.formulateEmptyToError(replaceRegexEmptyToRule))
      ),
      Arguments.of(
        List.of(replaceRegexEmptyFromRule),
        List.of(LocationsNotBlankRuleValidator.formulateEmptyFromError(replaceRegexEmptyFromRule))
      ),
      Arguments.of(
        List.of(replaceRegexEmptyPathRule),
        List.of(PathIsNotBlankRuleValidator.formulateEmptyPathError(replaceRegexEmptyPathRule))
      ),
      Arguments.of(
        List.of(replaceEmptyValueRule),
        List.of(ValueIsNotBlankRuleValidator.formulateEmptyValueError(replaceEmptyValueRule))
      ),
      Arguments.of(
        List.of(
          replaceRegexEmptyToRule,
          replaceRegexEmptyFromRule,
          replaceRegexEmptyPathRule,
          replaceEmptyValueRule
        ),
        List.of(
          LocationsNotBlankRuleValidator.formulateEmptyToError(replaceRegexEmptyToRule),
          LocationsNotBlankRuleValidator.formulateEmptyFromError(replaceRegexEmptyFromRule),
          PathIsNotBlankRuleValidator.formulateEmptyPathError(replaceRegexEmptyPathRule),
          ValueIsNotBlankRuleValidator.formulateEmptyValueError(replaceEmptyValueRule)
        )
      ),
      Arguments.of(
        List.of(replaceRegexCorrectRule),
        List.of()
      )
    );
  }
}
