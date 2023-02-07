package dev.alvo.jsontransform.eval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import dev.alvo.jsontransform.JsonProcessingRule;
import dev.alvo.jsontransform.read.FileJsonProcessingRuleReader;
import dev.alvo.jsontransform.read.JsonProcessingRuleParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

import static dev.alvo.jsontransform.JsonProcessingOperation.COPY;
import static dev.alvo.jsontransform.JsonProcessingOperation.DELETE;
import static dev.alvo.jsontransform.JsonProcessingOperation.REPLACE_REGEX;
import static dev.alvo.utils.Utils.fold;

class TransformingJsonProcessingRuleEvaluatorTest {

  private DirectJsonProcessingRuleEvaluator<String> evaluator;
  private ObjectMapper mapper;
  private FileJsonProcessingRuleReader rulesReader;

  //language=JSON
  private final String json =
    "{\n" +
      "  \"store\": {\n" +
      "    \"book\": [\n" +
      "      {\n" +
      "        \"rngCategory\": \"reference\",\n" +
      "        \"author\": \"Nigel Rees\",\n" +
      "        \"title\": \"Sayings of the Century\",\n" +
      "        \"price\": 8.95\n" +
      "      }\n" +
      "    ]\n" +
      "  }\n" +
      "}";

  @BeforeEach
  void setUp() {
    this.evaluator = new TransformingJsonProcessingRuleEvaluator();
    this.mapper = new ObjectMapper();
    this.rulesReader = new FileJsonProcessingRuleReader(new JsonProcessingRuleParser(mapper));
  }

  @Test
  void replaceRegexCorrectlyReplacingValue() {
    final var path = JsonPath.compile("$.*");
    final var rule = new JsonProcessingRule(REPLACE_REGEX, path, "\"rng", "\"cmb");
    final var actual = evaluator.evaluate(json, rule);

    System.out.println(actual);

    Assertions.assertTrue(actual.contains("cmbCategory"));
    Assertions.assertFalse(actual.contains("rngCategory"));
  }

  @Test
  void copyCorrectlyDuplicatesValue() {
    final var path = JsonPath.compile("$..store.book.[*]");
    final var rule = new JsonProcessingRule(
      COPY,
      path,
      "$.rngCategory",
      "$.category");

    final var actual = evaluator.evaluate(json, rule);

    Assertions.assertTrue(actual.contains("category"));
  }

  @Test
  void deleteCorrectlyRemovesValue() {
    final var path = JsonPath.compile("$..store.book.[*].rngCategory");
    final var rule = new JsonProcessingRule(DELETE, path);
    final var actual = evaluator.evaluate(json, rule);

    Assertions.assertFalse(actual.contains("rngCategory"));
  }

  @Test
  void addCorrectlyAppendsNode() {
    final var path = JsonPath.compile("$..store.book");
    final var rule = new JsonProcessingRule(DELETE, path);
    final var actual = evaluator.evaluate(json, rule);

    Assertions.assertFalse(actual.contains("rngCategory"));
  }

  @Test
  void userPreferencesMigrationIsCorrect() throws IOException, URISyntaxException {
    final var originalLocation = locateResource("/original.json");
    final var expectedLocation = locateResource("/expected.json");
    final var rulesLocation = locateResource("/replace-rules.json");
    final var original = readResource(originalLocation);
    final var expected = readResource(expectedLocation);

    rulesReader.read(new File(rulesLocation)).ifPresentOrElse(rules -> {
      final var actual = fold(rules, original, evaluator::evaluate);

      try {
        final var actualPreferences = mapper.readTree(actual);
        final var expectedPreferences = mapper.readTree(expected);

        Assertions.assertEquals(expectedPreferences, actualPreferences);
      } catch (JsonProcessingException e) {
        Assertions.fail("Error parsing expected or actual preference json");
      }
    }, () -> Assertions.fail("Was not able to load transformation rules"));
  }

  private String readResource(final URI location) throws IOException {
    return new String(Files.readAllBytes(new File((location)).toPath()));
  }

  private URI locateResource(final String path) throws URISyntaxException {
    return Objects.requireNonNull(getClass().getResource(path)).toURI();
  }
}
