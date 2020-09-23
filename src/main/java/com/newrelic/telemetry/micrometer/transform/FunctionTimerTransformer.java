/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.newrelic.telemetry.micrometer.transform;

import static java.util.Collections.singleton;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.Summary;
import com.newrelic.telemetry.micrometer.util.TimeTracker;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import java.util.Collection;

public class FunctionTimerTransformer {

  private final TimeTracker timeTracker;
  private final AttributesMaker attributesMaker = new AttributesMaker();

  public FunctionTimerTransformer(TimeTracker timeTracker) {
    this.timeTracker = timeTracker;
  }

  public Collection<Metric> transform(FunctionTimer functionTimer) {
    Meter.Id id = functionTimer.getId();
    long now = timeTracker.getCurrentTime();
    Attributes attributes = attributesMaker.make(id, "functionTimer");

    Summary summary =
        new Summary(
            id.getName(),
            (int) functionTimer.count(),
            functionTimer.totalTime(functionTimer.baseTimeUnit()),
            Double.NaN,
            Double.NaN,
            timeTracker.getPreviousTime(),
            now,
            attributes);

    return singleton(summary);
  }
}
