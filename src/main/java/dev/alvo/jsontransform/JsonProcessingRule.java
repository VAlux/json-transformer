package dev.alvo.jsontransform;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jayway.jsonpath.JsonPath;

import java.util.Objects;

import static dev.alvo.utils.Utils.getOrNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonProcessingRule {
  private final JsonProcessingOperation operation;
  private final JsonPath path;
  private String from;
  private String to;
  private String value;

  public JsonProcessingRule(JsonProcessingOperation operation, JsonPath path) {
    this.operation = operation;
    this.path = path;
  }

  public JsonProcessingRule(JsonProcessingOperation operation, String path) {
    this(operation, JsonPath.compile(path));
  }

  public JsonProcessingRule(JsonProcessingOperation operation, JsonPath path, String value) {
    this(operation, path);
    this.value = value;
  }

  public JsonProcessingRule(JsonProcessingOperation operation, String path, String value) {
    this(operation, JsonPath.compile(path), value);
  }

  public JsonProcessingRule(JsonProcessingOperation operation, JsonPath path, String from, String to) {
    this(operation, path);
    this.from = from;
    this.to = to;
  }

  public JsonProcessingRule(JsonProcessingOperation operation, String path, String from, String to) {
    this(operation, JsonPath.compile(path), from, to);
  }

  @JsonCreator
  public JsonProcessingRule(@JsonProperty("operation") JsonProcessingOperation operation,
                            @JsonProperty("path") String path,
                            @JsonProperty("from") String from,
                            @JsonProperty("to") String to,
                            @JsonProperty("value") String value) {
    this(operation, JsonPath.compile(path), from, to);
    this.value = value;
  }

  public JsonProcessingOperation getOperation() {
    return operation;
  }

  public JsonPath getPath() {
    return path;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JsonProcessingRule that = (JsonProcessingRule) o;
    return operation == that.operation
      && Objects.equals(path.getPath(), that.path.getPath())
      && Objects.equals(from, that.from)
      && Objects.equals(to, that.to)
      && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(operation, path, from, to, value);
  }

  @Override
  public String toString() {
    return "JsonProcessingRule{" +
      "operation=" + operation +
      ", path=" + getOrNull(path, JsonPath::getPath) +
      ", from='" + from + '\'' +
      ", to='" + to + '\'' +
      ", value='" + value + '\'' +
      '}';
  }
}
