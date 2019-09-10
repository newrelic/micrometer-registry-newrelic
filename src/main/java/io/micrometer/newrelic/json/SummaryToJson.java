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

import com.newrelic.telemetry.metrics.Summary;
import java.util.function.Function;

public class SummaryToJson implements Function<Summary, String> {

  @Override
  public String apply(Summary summary) {
    StringBuilder buff = new StringBuilder();
    object(
        buff,
        () -> {
          string(buff, "name", summary.getName());
          string(buff, "type", "summary");
          object(
              buff,
              "value",
              () -> {
                number(buff, "count", summary.getCount());
                number(buff, "sum", summary.getSum());
                number(buff, "min", summary.getMin());
                number(buff, "max", summary.getMax(), false);
              });
          number(buff, "timestamp", summary.getStartTimeMs());
          number(buff, "interval.ms", summary.getEndTimeMs() - summary.getStartTimeMs(), false);
          writeAttributes(buff, summary.getAttributes());
        },
        false);
    return buff.toString();
  }
}
