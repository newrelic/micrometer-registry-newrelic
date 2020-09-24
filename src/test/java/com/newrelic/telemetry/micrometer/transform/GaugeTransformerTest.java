/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.internal.DefaultGauge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GaugeTransformerTest {

  private final String GAUGE_NAME = "errorcount";
  private final String SOURCE_TYPE = "gauge";
  private final Tags TAGS = Tags.of("key1", "value1", "key2", "value2");

  private Clock clock;
  private long now;
  private GaugeTransformer testClass;
  private Attributes expectedAttrs;
  private AttributesMaker attributesMaker;

  @BeforeEach
  void setup() {
    clock = mock(Clock.class);
    now = System.currentTimeMillis();
    attributesMaker = mock(AttributesMaker.class);
    expectedAttrs = new Attributes().put("type", SOURCE_TYPE).put("foo", "bar");
    testClass = new GaugeTransformer(clock, attributesMaker);
  }

  @Test
  void testTransform() {
    Gauge micrometerGauge = buildGauge();

    when(attributesMaker.make(micrometerGauge.getId(), SOURCE_TYPE)).thenReturn(expectedAttrs);
    when(clock.wallTime()).thenReturn(now);

    com.newrelic.telemetry.metrics.Gauge result = testClass.transform(micrometerGauge);
    com.newrelic.telemetry.metrics.Gauge expectedGauge =
        new com.newrelic.telemetry.metrics.Gauge(GAUGE_NAME, 77.3, now, expectedAttrs);

    assertEquals(expectedGauge, result);
  }

  private Gauge buildGauge() {
    Meter.Id id = new Meter.Id(GAUGE_NAME, TAGS, "fathoms", "my super wonderful meter", Type.GAUGE);
    return new DefaultGauge<>(id, "some object", x -> 77.3);
  }
}
