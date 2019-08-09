/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import static io.micrometer.newrelic.json.JsonUtil.object;
import static io.micrometer.newrelic.json.JsonUtil.unknownType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AttributesWriter {

  public static void writeAttributesObject(StringBuilder buff, Map<String, Object> attributes) {
    object(buff, () -> writeAttributesInner(buff, attributes), false);
  }

  public static void writeAttributes(StringBuilder buff, Map<String, Object> attributes) {
    if (attributes.isEmpty()) {
      return;
    }
    buff.append(",");
    object(buff, "attributes", () -> writeAttributesInner(buff, attributes), false);
  }

  private static void writeAttributesInner(StringBuilder buff, Map<String, Object> attributes) {
    List<Entry<String, Object>> entries = new ArrayList<>(attributes.entrySet());
    entries
        .subList(0, entries.size() - 1)
        .forEach(
            entry -> {
              unknownType(buff, entry.getKey(), entry.getValue());
            });
    Entry<String, Object> last = entries.get(attributes.size() - 1);
    unknownType(buff, last.getKey(), last.getValue(), false);
  }
}
