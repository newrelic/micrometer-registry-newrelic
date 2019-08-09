/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Metric;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.noop.NoopTimeGauge;
import org.junit.jupiter.api.Test;

class TimeGaugeTransformerTest {

  @Test
  void testTransform() {
    Attributes expectedAttributes =
        new Attributes()
            .put("description", "something")
            .put("source_type", "time_gauge")
            .put("base_unit", "b")
            .put("foo", "bar");
    long now = System.currentTimeMillis();
    Id id = new Id("aaa", Tags.of(Tag.of("foo", "bar")), "b", "something", Type.GAUGE);
    Clock clock = mock(Clock.class);
    when(clock.wallTime()).thenReturn(now);

    Metric expected = new Gauge("aaa", 21.33, clock.wallTime(), expectedAttributes);

    GaugeTransformer gaugeTransformer = new GaugeTransformer(clock, new AttributesMaker());
    TimeGauge gauge =
        new NoopTimeGauge(id) {
          @Override
          public double value() {
            return 21.33;
          }
        };

    TimeGaugeTransformer testClass = new TimeGaugeTransformer(gaugeTransformer);

    Metric result = testClass.transform(gauge);

    assertEquals(expected, result);
  }
}
