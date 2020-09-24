/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.json;

import static com.newrelic.telemetry.micrometer.json.JsonUtil.number;
import static com.newrelic.telemetry.micrometer.json.JsonUtil.object;
import static com.newrelic.telemetry.micrometer.json.JsonUtil.string;

import com.newrelic.telemetry.metrics.Count;
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
          AttributesWriter.writeAttributes(buff, count.getAttributes());
        },
        false);
    return buff.toString();
  }
}
