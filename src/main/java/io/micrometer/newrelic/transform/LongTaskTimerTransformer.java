/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Metric;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.LongTaskTimer;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class LongTaskTimerTransformer {

  private final Clock clock;
  private final AttributesMaker attributesMaker = new AttributesMaker();

  public LongTaskTimerTransformer(Clock clock) {
    this.clock = clock;
  }

  public Collection<Metric> transform(LongTaskTimer timer) {
    Attributes attributes =
        attributesMaker.make(timer.getId(), "long_task_timer").put("baseTimeUnit", "NANOSECONDS");
    Gauge activeTasks =
        new Gauge(
            timer.getId().getName() + ".active_tasks",
            timer.activeTasks(),
            clock.wallTime(),
            attributes);
    Gauge totalDuration =
        new Gauge(
            timer.getId().getName() + ".total_duration",
            timer.duration(TimeUnit.NANOSECONDS),
            clock.wallTime(),
            attributes);

    return Arrays.asList(activeTasks, totalDuration);
  }
}
