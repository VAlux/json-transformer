package dev.alvo.jsontransform.read;

import dev.alvo.jsontransform.JsonProcessingRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

public class FileJsonProcessingRuleReader implements JsonProcessingRuleReader<File> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileJsonProcessingRuleReader.class);

  private final JsonProcessingRuleParser parser;

  public FileJsonProcessingRuleReader(JsonProcessingRuleParser parser) {
    this.parser = parser;
  }

  @Override
  public Optional<List<JsonProcessingRule>> read(final File source) {
    try {
      return parser.parseList(new String(Files.readAllBytes(source.toPath())));
    } catch (IOException ex) {
      LOGGER.error("Error reading json processing rules list: {}", ex.getMessage(), ex);
      return Optional.empty();
    }
  }
}
