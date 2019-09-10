/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import org.junit.jupiter.api.Test;

class GaugeToJsonTest {

  @Test
  void testToJson() {
    GaugeToJson testClass = new GaugeToJson();
    Gauge gauge = new Gauge("bern", 88.9, 90210, new Attributes().put("foo", "bar"));
    String result = testClass.apply(gauge);
    assertEquals(
        "{\"name\":\"bern\",\"type\":\"gauge\",\"value\":88.9,\"timestamp\":90210,\"attributes\":{\"foo\":\"bar\"}}",
        result);
  }

  @Test
  void testNoAttrs() {
    GaugeToJson testClass = new GaugeToJson();
    Gauge gauge = new Gauge("bb", -12, 12987690210L, new Attributes());
    String result = testClass.apply(gauge);
    assertEquals(
        "{\"name\":\"bb\",\"type\":\"gauge\",\"value\":-12.0,\"timestamp\":12987690210}", result);
  }
}
