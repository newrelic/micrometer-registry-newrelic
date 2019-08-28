/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Count;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Metric;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import io.micrometer.newrelic.util.LastSeenTracker;
import java.util.stream.Stream;

public class HistogramSnapshotTransformer {
  private final LastSeenTracker lastSeenTracker;
  private final Clock clock;

  public HistogramSnapshotTransformer(LastSeenTracker lastSeenTracker, Clock clock) {
    this.lastSeenTracker = lastSeenTracker;
    this.clock = clock;
  }

  Stream<Metric> buildSnapshotSummary(HistogramSupport histogramSupport) {
    long now = clock.wallTime();
    HistogramSnapshot snapshot = histogramSupport.takeSnapshot();
    Attributes attributes = new Attributes().put("source.type", "distributionSummary");
    Meter.Id metricId = histogramSupport.getId();
    String countName = metricId.getName() + ".snapshot.count";
    LastSeenTracker.LastSeen lastSeen =
        lastSeenTracker.getAndUpdate(countName, now, snapshot.count(), 0);
    return Stream.of(
        new Count(
            countName, snapshot.count() - lastSeen.getValue(), lastSeen.getTime(), now, attributes),
        new Gauge(metricId.getName() + ".snapshot.total", snapshot.total(), now, attributes),
        new Gauge(metricId.getName() + ".snapshot.max", snapshot.max(), now, attributes));
  }
}
