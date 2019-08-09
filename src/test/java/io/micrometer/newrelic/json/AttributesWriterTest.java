/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

class AttributesWriterTest {

  @Test
  void testWrite() {
    StringBuilder buff = new StringBuilder();
    Map<String, Object> attrs = new TreeMap<>();
    attrs.put("foo", "bar");
    attrs.put("bar", 123);
    attrs.put("dare", false);
    attrs.put("mug", "choco");
    AttributesWriter.writeAttributes(buff, attrs);
    assertEquals(
        ",\"attributes\":{"
            + "\"bar\":123,"
            + "\"dare\":false,"
            + "\"foo\":\"bar\","
            + "\"mug\":\"choco\"}",
        buff.toString());
  }

  @Test
  void testWriteAttributesObject() {
    StringBuilder buff = new StringBuilder();
    Map<String, Object> attrs = new TreeMap<>();
    attrs.put("foo", "zip");
    attrs.put("bar", "zab");
    AttributesWriter.writeAttributesObject(buff, attrs);
    assertEquals("{\"bar\":\"zab\",\"foo\":\"zip\"}", buff.toString());
  }

  @Test
  void testSingleAttr() {
    StringBuilder buff = new StringBuilder();
    Map<String, Object> attrs = new TreeMap<>();
    attrs.put("a", "b");
    AttributesWriter.writeAttributes(buff, attrs);
    assertEquals(",\"attributes\":{\"a\":\"b\"}", buff.toString());
  }

  @Test
  void testEmpty() {
    StringBuilder buff = new StringBuilder();
    AttributesWriter.writeAttributes(buff, new TreeMap<>());
    assertEquals("", buff.toString());
  }
}
