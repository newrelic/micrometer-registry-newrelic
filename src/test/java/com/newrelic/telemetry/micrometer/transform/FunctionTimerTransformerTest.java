/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.telemetry.micrometer.transform;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.Summary;
import com.newrelic.telemetry.micrometer.util.TimeTracker;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
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
    when(timeTracker.getPreviousTime()).thenReturn(before);
    when(timeTracker.getCurrentTime()).thenReturn(now);

    Summary summary =
        new Summary(
            "functionTimerName",
            55,
            10000,
            Double.NaN,
            Double.NaN,
            before,
            now,
            expectedAttributes);
    Collection<Metric> metrics = functionTimerTransformer.transform(functionTimer);

    assertEquals(singleton(summary), metrics);
  }
}
