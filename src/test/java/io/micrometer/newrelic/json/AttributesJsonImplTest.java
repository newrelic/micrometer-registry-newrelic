package io.micrometer.newrelic.json;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;


class AttributesJsonImplTest {
  @Test
  void testEmptyMapToJson() {
    AttributesJsonImpl testClass = new AttributesJsonImpl();
    Map<String, Object> map = new HashMap<>();
    String result = testClass.toJson(map);
    assertEquals("{}", result);
  }

  @Test
  void testMapWithContentsToJson() {
    AttributesJsonImpl testClass = new AttributesJsonImpl();
    Map<String, Object> map = new HashMap<>();
    map.put("foo", "bar");
    map.put("hello", 12);
    String result = testClass.toJson(map);
    assertEquals("{ foo: bar, hello: 12 }", result);
  }
}