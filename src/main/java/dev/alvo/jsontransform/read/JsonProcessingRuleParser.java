package dev.alvo.jsontransform.read;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import dev.alvo.jsontransform.JsonProcessingRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class JsonProcessingRuleParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonProcessingRuleParser.class);

  private final ObjectMapper mapper;
  private final CollectionType processingRulesListType;

  public JsonProcessingRuleParser(ObjectMapper mapper) {
    this.mapper = mapper;
    this.processingRulesListType =
      mapper.getTypeFactory().constructCollectionType(List.class, JsonProcessingRule.class);
  }

  public Optional<JsonProcessingRule> parse(final String input) {
    try {
      return Optional.ofNullable(mapper.readValue(input, JsonProcessingRule.class));
    } catch (JsonProcessingException ex) {
      LOGGER.error("Error parsing json processing rule: {}", ex.getMessage(), ex);
      return Optional.empty();
    }
  }

  public Optional<List<JsonProcessingRule>> parseList(final String input) {
    try {
      return Optional.ofNullable(mapper.readValue(input, processingRulesListType));
    } catch (JsonProcessingException ex) {
      LOGGER.error("Error parsing json processing rule: {}", ex.getMessage(), ex);
      return Optional.empty();
    }
  }
}
