/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import static io.micrometer.newrelic.json.AttributesWriter.writeAttributes;
import static io.micrometer.newrelic.json.JsonUtil.number;
import static io.micrometer.newrelic.json.JsonUtil.object;
import static io.micrometer.newrelic.json.JsonUtil.string;

import com.newrelic.telemetry.Count;
import java.util.function.Function;

public class CountToJson implements Function<Count, String> {

  @Override
  public String apply(Count count) {
    StringBuilder buff = new StringBuilder();
    object(
        buff,
        () -> {
          string(buff, "name", count.getName());
          string(buff, "type", "count");
          number(buff, "value", count.getValue());
          number(buff, "timestamp", count.getStartTimeMs());
          number(buff, "interval.ms", count.getEndTimeMs() - count.getStartTimeMs(), false);
          writeAttributes(buff, count.getAttributes());
        },
        false);
    return buff.toString();
  }
}
