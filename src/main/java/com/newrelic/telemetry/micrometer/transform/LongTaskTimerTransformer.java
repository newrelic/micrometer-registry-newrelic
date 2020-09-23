/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.transform;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
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
        attributesMaker.make(timer.getId(), "longTaskTimer").put("baseTimeUnit", "NANOSECONDS");
    Gauge activeTasks =
        new Gauge(
            timer.getId().getName() + ".activeTasks",
            timer.activeTasks(),
            clock.wallTime(),
            attributes);
    Gauge totalDuration =
        new Gauge(
            timer.getId().getName() + ".totalDuration",
            timer.duration(TimeUnit.NANOSECONDS),
            clock.wallTime(),
            attributes);

    return Arrays.asList(activeTasks, totalDuration);
  }
}
