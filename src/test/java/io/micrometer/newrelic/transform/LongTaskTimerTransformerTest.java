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
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class LongTaskTimerTransformerTest {

  @Test
  void transform() {
    long now = System.currentTimeMillis();
    String name = "filbert p willikins";
    Id id = new Id(name, Tags.of(Tag.of("spoon", "pup")), "grams", "des des", Type.LONG_TASK_TIMER);
    Attributes expectedAttributes =
        new Attributes()
            .put("spoon", "pup")
            .put("description", "des des")
            .put("source.type", "longTaskTimer")
            .put("baseUnit", "grams")
            .put("baseTimeUnit", "NANOSECONDS");

    Clock clock = mock(Clock.class);
    LongTaskTimerTransformer testClass = new LongTaskTimerTransformer(clock);
    Metric active = new Gauge(name + ".activeTasks", 15, now, expectedAttributes);
    Metric totalDuration = new Gauge(name + ".totalDuration", 21.42, now, expectedAttributes);
    Collection<Metric> expected = Arrays.asList(active, totalDuration);

    LongTaskTimer timer = mock(LongTaskTimer.class);
    when(clock.wallTime()).thenReturn(now);
    when(timer.getId()).thenReturn(id);
    when(timer.activeTasks()).thenReturn(15);
    when(timer.duration(TimeUnit.NANOSECONDS)).thenReturn(21.42);

    Collection<Metric> results = testClass.transform(timer);

    assertEquals(expected, results);
  }
}
