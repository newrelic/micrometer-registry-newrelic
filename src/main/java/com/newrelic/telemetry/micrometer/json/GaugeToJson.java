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

import com.newrelic.telemetry.metrics.Gauge;
import java.util.function.Function;

public class GaugeToJson implements Function<Gauge, String> {

  @Override
  public String apply(Gauge gauge) {
    StringBuilder buff = new StringBuilder();
    object(
        buff,
        () -> {
          string(buff, "name", gauge.getName());
          string(buff, "type", "gauge");
          number(buff, "value", gauge.getValue());
          number(buff, "timestamp", gauge.getTimestamp(), false);
          AttributesWriter.writeAttributes(buff, gauge.getAttributes());
        },
        false);
    return buff.toString();
  }
}
