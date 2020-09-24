/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.json;

import static io.micrometer.core.instrument.util.StringEscapeUtils.escapeJson;

import java.util.function.Supplier;

class JsonUtil {

  static void object(StringBuilder buff, String name, Runnable body) {
    object(buff, name, body, true);
  }

  static void object(StringBuilder buff, String name, Runnable body, boolean trailingComma) {
    escQuote(buff, name);
    buff.append(":");
    object(buff, body, false);
    trailComma(buff, trailingComma);
  }

  static void object(StringBuilder buff, Runnable body) {
    object(buff, body, true);
  }

  static void object(StringBuilder buff, Runnable body, boolean trailingComma) {
    buff.append("{");
    body.run();
    buff.append("}");
    trailComma(buff, trailingComma);
  }

  static void unknownType(StringBuilder buff, String key, Object value) {
    unknownType(buff, key, value, true);
  }

  static void unknownType(StringBuilder buff, String key, Object value, boolean trailingComma) {
    if (value instanceof Boolean) {
      kv(buff, key, ((Boolean) value)::toString, trailingComma);
    } else if (value instanceof Number) {
      number(buff, key, (Number) value, trailingComma);
    } else {
      string(buff, key, String.valueOf(value), trailingComma);
    }
  }

  static void string(StringBuilder buff, String key, String value) {
    string(buff, key, value, true);
  }

  static void string(StringBuilder buff, String key, String value, boolean trailingComma) {
    kv(buff, key, () -> escQuote(value), trailingComma);
  }

  static void number(StringBuilder buff, String key, Number value) {
    number(buff, key, value, true);
  }

  static void number(StringBuilder buff, String key, Number value, boolean trailingComma) {
    kv(buff, key, value::toString, trailingComma);
  }

  private static void kv(
      StringBuilder buff, String key, Supplier<String> value, boolean trailingComma) {
    escQuote(buff, key);
    buff.append(":");
    buff.append(value.get());
    trailComma(buff, trailingComma);
  }

  private static String escQuote(String content) {
    return quote(escapeJson(content));
  }

  private static void escQuote(StringBuilder buff, String content) {
    quote(buff, escapeJson(content));
  }

  private static void quote(StringBuilder buff, String content) {
    buff.append(quote(content));
  }

  private static String quote(String content) {
    return "\"" + content + "\"";
  }

  private static void trailComma(StringBuilder buff, boolean trailingComma) {
    if (trailingComma) {
      buff.append(",");
    }
  }
}
