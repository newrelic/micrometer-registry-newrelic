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
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimerTransformerTest {

  @Mock private Timer timer;
  @Mock private TimeTracker timeTracker;

  @Test
  void testTransform() {
    long now = System.currentTimeMillis();
    Attributes standardAttributes =
        new Attributes()
            .put("foo", "bar")
            .put("description", "description")
            .put("baseUnit", "units");

    Summary summary =
        new Summary(
            "timerName", 1000, 20000, Double.NaN, 5000, now - 2000, now, standardAttributes);

    when(timeTracker.getCurrentTime()).thenReturn(now);
    when(timeTracker.getPreviousTime()).thenReturn(now - 2000);

    when(timer.getId())
        .thenReturn(
            new Meter.Id(
                "timerName", Tags.of("foo", "bar"), "units", "description", Meter.Type.TIMER));
    when(timer.count()).thenReturn(1000L);
    when(timer.totalTime(TimeUnit.SECONDS)).thenReturn(20000d);
    when(timer.baseTimeUnit()).thenReturn(TimeUnit.SECONDS);
    when(timer.max(TimeUnit.SECONDS)).thenReturn(5000d);

    TimerTransformer timerTransformer = new TimerTransformer(timeTracker);
    Collection<Metric> results = timerTransformer.transform(timer);

    Collection<Metric> expected = singleton(summary);
    assertEquals(expected, new HashSet<>(results));
  }

  @Test
  void testSkippingGaugesWhenCountIsZero() {
    long now = System.currentTimeMillis();
    Attributes standardAttributes =
        new Attributes()
            .put("foo", "bar")
            .put("description", "description")
            .put("baseUnit", "units");

    Summary summary =
        new Summary("timerName", 0, 0, Double.NaN, 0, now - 2000, now, standardAttributes);

    when(timeTracker.getCurrentTime()).thenReturn(now);
    when(timeTracker.getPreviousTime()).thenReturn(now - 2000);

    when(timer.getId())
        .thenReturn(
            new Meter.Id(
                "timerName", Tags.of("foo", "bar"), "units", "description", Meter.Type.TIMER));
    when(timer.count()).thenReturn(0L);

    TimerTransformer timerTransformer = new TimerTransformer(timeTracker);
    Collection<Metric> results = timerTransformer.transform(timer);

    Collection<Metric> expected = singleton(summary);
    assertEquals(expected, new HashSet<>(results));
  }
}
