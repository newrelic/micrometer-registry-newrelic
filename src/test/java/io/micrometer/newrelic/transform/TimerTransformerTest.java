/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.newrelic.util.TimeTracker;
import java.util.Arrays;
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
            .put("source.type", "timer")
            .put("description", "description")
            .put("baseUnit", "units");
    Count count = new Count("timerName.count", 1000, now - 2000, now, standardAttributes);
    Gauge totalTime = new Gauge("timerName.totalTime", 20000, now, standardAttributes);
    Gauge max = new Gauge("timerName.max", 5000, now, standardAttributes);
    Gauge mean = new Gauge("timerName.mean", 500, now, standardAttributes);

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
    when(timer.mean(TimeUnit.SECONDS)).thenReturn(500d);

    TimerTransformer timerTransformer = new TimerTransformer(timeTracker);
    Collection<Metric> results = timerTransformer.transform(timer);

    Collection<Metric> expected = new HashSet<>(Arrays.asList(count, totalTime, max, mean));
    assertEquals(expected, new HashSet<>(results));
  }
}
