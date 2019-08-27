package io.micrometer.newrelic.json;

import com.newrelic.telemetry.AttributesJson;
import java.util.Map;

public class AttributesJsonImpl implements AttributesJson {

  @Override
  public String toJson(Map<String, Object> attributes) {
    if (attributes.isEmpty()) {
      return "{}";
    }
    StringBuilder builder = new StringBuilder();
    AttributesWriter.writeAttributesObject(builder, attributes);
    return builder.toString();
  }
}
