/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import static io.micrometer.newrelic.json.JsonUtil.number;
import static io.micrometer.newrelic.json.JsonUtil.object;
import static io.micrometer.newrelic.json.JsonUtil.string;
import static io.micrometer.newrelic.json.JsonUtil.unknownType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

  private StringBuilder buff;

  @BeforeEach
  void setup() {
    buff = new StringBuilder();
  }

  @Test
  void objectWithNoNameNoComma() {
    object(buff, () -> buff.append("ok"), false);
    assertEquals("{ok}", buff.toString());
  }

  @Test
  void objectWithNameNoComma() {
    object(buff, "shelly", () -> buff.append("ok"), false);
    assertEquals("\"shelly\":{ok}", buff.toString());
  }

  @Test
  void objectWithNoNameComma() {
    object(buff, () -> buff.append("ok"));
    assertEquals("{ok},", buff.toString());
  }

  @Test
  void objectWithNameComma() {
    object(buff, "shelly", () -> buff.append("ok"));
    assertEquals("\"shelly\":{ok},", buff.toString());
  }

  @Test
  void unknownTypeString() {
    unknownType(buff, "kid", "icarus");
    assertEquals("\"kid\":\"icarus\",", buff.toString());
  }

  @Test
  void unknownTypeNumberNoComma() {
    unknownType(buff, "kid", 21, false);
    assertEquals("\"kid\":21", buff.toString());
  }

  @Test
  void unknownTypeNumberComma() {
    unknownType(buff, "kid", 21);
    assertEquals("\"kid\":21,", buff.toString());
  }

  @Test
  void unknownTypeBooleanNoComma() {
    unknownType(buff, "isIt", true, false);
    assertEquals("\"isIt\":true", buff.toString());
  }

  @Test
  void unknownTypeBooleanComma() {
    unknownType(buff, "isIt", true);
    assertEquals("\"isIt\":true,", buff.toString());
  }

  @Test
  void unknownTypeNonstandard() {
    unknownType(
        buff,
        "random",
        new Object() {
          @Override
          public String toString() {
            return "pie";
          }
        });
    assertEquals("\"random\":\"pie\",", buff.toString());
  }

  @Test
  void stringNoComma() {
    string(buff, "name", "joe", false);
    assertEquals("\"name\":\"joe\"", buff.toString());
  }

  @Test
  void stringWithComma() {
    string(buff, "name", "shemp");
    assertEquals("\"name\":\"shemp\",", buff.toString());
  }

  @Test
  void testStringIsEscaped() {
    string(buff, "a \"special\" key", "a \"really \t\uD83C\uDF54 special\" value", false);
    assertEquals(
        "\"a \\\"special\\\" key\":\"a \\\"really \\t\uD83C\uDF54 special\\\" value\"",
        buff.toString());
  }

  @Test
  void numberNoComma() {
    number(buff, "height", 21.6, false);
    assertEquals("\"height\":21.6", buff.toString());
  }

  @Test
  void numberWithComma() {
    number(buff, "height", 11);
    assertEquals("\"height\":11,", buff.toString());
  }
}
