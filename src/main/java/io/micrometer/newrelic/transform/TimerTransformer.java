/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import static java.util.Collections.singleton;

import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.newrelic.util.TimeTracker;
import java.util.Arrays;
import java.util.Collection;

public class TimerTransformer {
  private final AttributesMaker attributesMaker = new AttributesMaker();

  private final TimeTracker timeTracker;

  public TimerTransformer(TimeTracker timeTracker) {
    this.timeTracker = timeTracker;
  }

  public Collection<Metric> transform(Timer timer) {

    Meter.Id id = timer.getId();
    long now = timeTracker.getCurrentTime();
    Count count =
        new Count(
            id.getName() + ".count",
            timer.count(),
            timeTracker.getPreviousTime(),
            now,
            attributesMaker.make(id, "timer"));
    if (timer.count() <= 0) {
      return singleton(count);
    }
    Gauge total =
        new Gauge(
            id.getName() + ".totalTime",
            timer.totalTime(timer.baseTimeUnit()),
            now,
            attributesMaker.make(id, "timer"));
    Gauge max =
        new Gauge(
            id.getName() + ".max",
            timer.max(timer.baseTimeUnit()),
            now,
            attributesMaker.make(id, "timer"));
    Gauge mean =
        new Gauge(
            id.getName() + ".mean",
            timer.mean(timer.baseTimeUnit()),
            now,
            attributesMaker.make(id, "timer"));
    return Arrays.asList(count, total, max, mean);
  }
}
