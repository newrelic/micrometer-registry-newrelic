/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.newrelic.util.TimeTracker;
import java.util.Arrays;
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
    Count count =
        new Count(
            id.getName() + ".count",
            functionTimer.count(),
            timeTracker.getPreviousTime(),
            now,
            attributes);
    Gauge totalTime =
        new Gauge(
            id.getName() + ".totalTime",
            functionTimer.totalTime(functionTimer.baseTimeUnit()),
            now,
            attributes);
    Gauge mean =
        new Gauge(
            id.getName() + ".mean",
            functionTimer.mean(functionTimer.baseTimeUnit()),
            now,
            attributes);
    return Arrays.asList(count, totalTime, mean);
  }
}
