/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Count;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Summary;
import org.junit.jupiter.api.Test;

class MetricToJsonTest {

  @Test
  void testWriteSummaryJson() {
    Summary summary = new Summary("xx", 12, 13, 14, 15, 16, 17, new Attributes().put("a", "b"));
    MetricToJson testClass =
        new MetricToJson(
            s -> {
              assertEquals(summary, s);
              return "expected $$$";
            },
            null,
            null);
    assertEquals("expected $$$", testClass.writeSummaryJson(summary));
  }

  @Test
  void testWriteCountJson() {
    Count count = new Count("yyyy", 7, 8, 9, new Attributes().put("hi", "bye"));
    MetricToJson testClass =
        new MetricToJson(
            null,
            ct -> {
              assertEquals(count, ct);
              return "expected $$$";
            },
            null);
    assertEquals("expected $$$", testClass.writeCountJson(count));
  }

  @Test
  void testWriteGaugeJson() {
    Gauge gauge = new Gauge("x", 999999, 888888, new Attributes().put("b", "c"));
    MetricToJson testClass =
        new MetricToJson(
            null,
            null,
            s -> {
              assertEquals(gauge, s);
              return "expected $$$";
            });
    assertEquals("expected $$$", testClass.writeGaugeJson(gauge));
  }
}
