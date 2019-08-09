/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Metric;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Statistic;
import io.micrometer.core.instrument.Tags;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BareMeterTransformerTest {

  @Mock private Clock clock;

  @Test
  void testTransform() throws Exception {
    long now = System.currentTimeMillis();

    Meter meter = mock(Meter.class);

    when(clock.wallTime()).thenReturn(now);
    when(meter.getId())
        .thenReturn(
            new Meter.Id(
                "meterName", Tags.of("foo", "bar"), "units", "meterDescription", Meter.Type.OTHER));
    when(meter.measure())
        .thenReturn(
            Arrays.asList(
                new Measurement(() -> 55d, Statistic.COUNT),
                new Measurement(() -> 34.44d, Statistic.UNKNOWN),
                new Measurement(() -> Double.NEGATIVE_INFINITY, Statistic.ACTIVE_TASKS)));

    Attributes expectedAttributes =
        new Attributes()
            .put("source_type", "meter")
            .put("base_unit", "units")
            .put("description", "meterDescription")
            .put("foo", "bar");
    Gauge count = new Gauge("meterName.count", 55d, now, expectedAttributes);
    Gauge unknown = new Gauge("meterName.unknown", 34.44d, now, expectedAttributes);

    Set<Metric> expected = Stream.of(count, unknown).collect(toSet());

    Collection<Metric> metrics = new BareMeterTransformer(clock).transform(meter);
    assertEquals(expected, new HashSet<>(metrics));
  }
}
