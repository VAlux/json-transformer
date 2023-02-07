package dev.alvo.jsontransform.read;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alvo.jsontransform.JsonProcessingRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static dev.alvo.jsontransform.JsonProcessingOperation.ADD;
import static dev.alvo.jsontransform.JsonProcessingOperation.COPY;
import static dev.alvo.jsontransform.JsonProcessingOperation.DELETE;
import static dev.alvo.jsontransform.JsonProcessingOperation.MOVE;
import static dev.alvo.jsontransform.JsonProcessingOperation.REPLACE;
import static dev.alvo.jsontransform.JsonProcessingOperation.REPLACE_REGEX;

class JsonProcessingRuleParserTest {

  private JsonProcessingRuleParser parser;

  @BeforeEach
  void setUp() {
    this.parser = new JsonProcessingRuleParser(new ObjectMapper());
  }

  @ParameterizedTest
  @MethodSource("generateParseRuleCases")
  void parsesRuleCorrectly(String json, JsonProcessingRule expectedRule) {
    System.out.println("parsing " + json + " to get " + expectedRule.toString());
    parser.parse(json)
      .ifPresentOrElse(
        rule -> Assertions.assertEquals(expectedRule, rule),
        () -> Assertions.fail("Error parsing json processing rule"));
  }

  public static Stream<Arguments> generateParseRuleCases() {
    return Stream.of(
      // REPLACE_REGEX
      Arguments.of(
        //language=JSON
        "{\n" +
          "  \"operation\": \"REPLACE_REGEX\",\n" +
          "  \"path\": \"$\",\n" +
          "  \"from\": \"\\\"rng\",\n" +
          "  \"to\": \"\\\"cmb\"\n" +
          "}\n",
        new JsonProcessingRule(REPLACE_REGEX, "$", "\"rng", "\"cmb")
      ),
      // REPLACE
      Arguments.of(
        //language=JSON
        "{\n" +
          "  \"operation\": \"REPLACE\",\n" +
          "  \"path\": \"$\",\n" +
          "  \"value\": \"{\\\"test\\\": 1}\"\n" +
          "}\n",
        new JsonProcessingRule(REPLACE, "$", "{\"test\": 1}")
      ),
      // ADD
      Arguments.of(
        //language=JSON
        "{\n" +
          "  \"operation\": \"ADD\",\n" +
          "  \"path\": \"$\",\n" +
          "  \"value\": \"{\\\"test\\\": 1}\"\n" +
          "}\n",
        new JsonProcessingRule(ADD, "$", "{\"test\": 1}")
      ),
      // COPY
      Arguments.of(
        //language=JSON
        "{\n" +
          "  \"operation\": \"COPY\",\n" +
          "  \"path\": \"$\",\n" +
          "  \"from\": \"$..test.value\",\n" +
          "  \"to\": \"$..test.new\"\n" +
          "}\n",
        new JsonProcessingRule(COPY, "$", "$..test.value", "$..test.new")
      ),
      // MOVE
      Arguments.of(
        //language=JSON
        "{\n" +
          "  \"operation\": \"MOVE\",\n" +
          "  \"path\": \"$\",\n" +
          "  \"from\": \"$..test.value\",\n" +
          "  \"to\": \"$..test.new\"\n" +
          "}\n",
        new JsonProcessingRule(MOVE, "$", "$..test.value", "$..test.new")
      ),
      // DELETE
      Arguments.of(
        //language=JSON
        "{\n" +
          "  \"operation\": \"DELETE\",\n" +
          "  \"path\": \"$..test.value\"\n" +
          "}\n",
        new JsonProcessingRule(DELETE, "$..test.value")
      )
    );
  }
}
