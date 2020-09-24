/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.telemetry.micrometer.transform;

import static java.util.Collections.singleton;

import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.Summary;
import com.newrelic.telemetry.micrometer.util.TimeTracker;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
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

    // we have all the data but a `min`, so just send in NaN for that value and the SDK will turn
    // it into a null.
    Summary summary =
        new Summary(
            id.getName(),
            (int) timer.count(),
            timer.totalTime(timer.baseTimeUnit()),
            Double.NaN,
            timer.max(timer.baseTimeUnit()),
            timeTracker.getPreviousTime(),
            now,
            attributesMaker.make(id, "timer"));

    return singleton(summary);
  }
}
