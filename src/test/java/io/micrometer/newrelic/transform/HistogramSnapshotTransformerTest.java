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
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.newrelic.util.LastSeenTracker;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HistogramSnapshotTransformerTest {
  @Mock private LastSeenTracker lastSeenTracker;
  @Mock private HistogramSupport histogramSupport;
  @Mock private Clock clock;

  @Test
  void testTransform() {
    String metricName = "spoon";
    long now = System.currentTimeMillis();

    long before = now - 15000;

    int oldSnapshotCount = 441;
    int snapshotCount = 444;
    double snapshotTotal = 99.1;
    double snapshotMax = 2.31;
    LastSeenTracker.LastSeen lastSeenSnapshot =
        new LastSeenTracker.LastSeen(before, oldSnapshotCount, 0);

    Meter.Id summaryId =
        new Meter.Id(
            metricName,
            Tags.of(Tag.of("Foo", "bAR")),
            "cheese",
            "some cheese",
            Meter.Type.DISTRIBUTION_SUMMARY);

    ValueAtPercentile[] valuePercentiles =
        new ValueAtPercentile[] {
          new ValueAtPercentile(21.666, 666.999), new ValueAtPercentile(69.420, 49.620)
        };

    Metric expectedSnapshotCount =
        new Count(
            metricName + ".snapshot.count",
            snapshotCount - oldSnapshotCount,
            before,
            now,
            new Attributes());
    Metric expectedSnapshotTotal =
        new Gauge(metricName + ".snapshot.total", snapshotTotal, now, new Attributes());
    Metric expectedSnapshotMax =
        new Gauge(metricName + ".snapshot.max", snapshotMax, now, new Attributes());

    Collection<Metric> expected =
        Arrays.asList(expectedSnapshotCount, expectedSnapshotTotal, expectedSnapshotMax);

    HistogramSnapshotTransformer snapshotTransformer =
        new HistogramSnapshotTransformer(lastSeenTracker, clock);

    HistogramSnapshot snapshot =
        new HistogramSnapshot(
            snapshotCount, snapshotTotal, snapshotMax, valuePercentiles, null, null);

    when(clock.wallTime()).thenReturn(now);
    when(histogramSupport.getId()).thenReturn(summaryId);
    when(histogramSupport.takeSnapshot()).thenReturn(snapshot);
    when(lastSeenTracker.getAndUpdate(metricName + ".snapshot.count", now, snapshotCount, 0))
        .thenReturn(lastSeenSnapshot);

    Stream<Metric> result = snapshotTransformer.buildSnapshotSummary(histogramSupport);
    assertEquals(expected, result.collect(Collectors.toList()));
  }
}
