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

import com.newrelic.telemetry.Gauge;
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
          writeAttributes(buff, gauge.getAttributes());
        },
        false);
    return buff.toString();
  }
}
