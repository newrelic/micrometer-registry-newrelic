/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Count;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Metric;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.newrelic.util.TimeTracker;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FunctionTimerTransformerTest {

  @Mock private FunctionTimer functionTimer;
  @Mock private TimeTracker timeTracker;

  @Test
  void testTransform() {
    FunctionTimerTransformer functionTimerTransformer = new FunctionTimerTransformer(timeTracker);
    long now = System.currentTimeMillis();
    long before = now - 9999;
    Attributes expectedAttributes =
        new Attributes()
            .put("description", "timerDescription")
            .put("source.type", "function_timer")
            .put("baseUnit", "tomatoes")
            .put("foo", "bar");

    Meter.Id id =
        new Meter.Id(
            "functionTimerName",
            Tags.of("foo", "bar"),
            "tomatoes",
            "timerDescription",
            Meter.Type.OTHER);
    when(functionTimer.baseTimeUnit()).thenReturn(TimeUnit.NANOSECONDS);
    when(functionTimer.getId()).thenReturn(id);
    when(functionTimer.count()).thenReturn(55d);
    when(functionTimer.totalTime(TimeUnit.NANOSECONDS)).thenReturn(10000d);
    when(functionTimer.mean(TimeUnit.NANOSECONDS)).thenReturn(50d);
    when(timeTracker.getPreviousTime()).thenReturn(before);
    when(timeTracker.getCurrentTime()).thenReturn(now);

    Count count = new Count("functionTimerName.count", 55L, before, now, expectedAttributes);
    Gauge totalTime = new Gauge("functionTimerName.total_time", 10000L, now, expectedAttributes);
    Gauge mean = new Gauge("functionTimerName.mean", 50L, now, expectedAttributes);
    Collection<Metric> metrics = functionTimerTransformer.transform(functionTimer);

    Set<Metric> expected = Stream.of(count, totalTime, mean).collect(toSet());
    assertEquals(expected, new HashSet<>(metrics));
  }
}
