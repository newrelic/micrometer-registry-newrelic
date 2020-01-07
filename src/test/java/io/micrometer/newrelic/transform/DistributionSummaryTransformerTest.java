/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.micrometer.newrelic.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.Summary;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.newrelic.util.TimeTracker;
import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DistributionSummaryTransformerTest {

  @Test
  void testTransform() {
    String metricName = "spoon";
    long now = System.currentTimeMillis();
    long before = now - 15000;

    int snapshotCount = 444;
    double snapshotTotal = 99.1;
    double snapshotMax = 2.31;

    Id summaryId =
        new Id(
            metricName,
            Tags.of(Tag.of("Foo", "bAR")),
            "cheese",
            "some cheese",
            Type.DISTRIBUTION_SUMMARY);

    DistributionSummary summary = mock(DistributionSummary.class);

    ValueAtPercentile[] valuePercentiles =
        new ValueAtPercentile[] {
          new ValueAtPercentile(21.666, 666.999), new ValueAtPercentile(69.420, 49.620)
        };
    Attributes summaryAttributes = new Attributes().put("stuff", "things");

    Metric expectedSummary =
        new Summary(
            metricName + ".summary",
            snapshotCount,
            snapshotTotal,
            Double.NaN,
            snapshotMax,
            before,
            now,
            summaryAttributes);

    Metric expectedPercentile1 =
        new Gauge(
            metricName + ".percentiles",
            valuePercentiles[0].value(),
            now,
            new Attributes().put("percentile", valuePercentiles[0].percentile() * 100d));
    Metric expectedPercentile2 =
        new Gauge(
            metricName + ".percentiles",
            valuePercentiles[1].value(),
            now,
            new Attributes().put("percentile", valuePercentiles[1].percentile() * 100d));

    Collection<Metric> expected =
        Arrays.asList(expectedSummary, expectedPercentile1, expectedPercentile2);

    TimeTracker timeTracker = mock(TimeTracker.class);
    AttributesMaker attributesMaker = mock(AttributesMaker.class);

    HistogramSnapshot snapshot =
        new HistogramSnapshot(
            snapshotCount, snapshotTotal, snapshotMax, valuePercentiles, null, null);

    when(timeTracker.getCurrentTime()).thenReturn(now);
    when(timeTracker.getPreviousTime()).thenReturn(before);
    when(attributesMaker.make(summaryId, "distributionSummary")).thenReturn(summaryAttributes);
    when(summary.getId()).thenReturn(summaryId);
    when(summary.takeSnapshot()).thenReturn(snapshot);

    DistributionSummaryTransformer testClass =
        new DistributionSummaryTransformer(timeTracker, attributesMaker);
    Collection<Metric> result = testClass.transform(summary);
    assertEquals(expected, result);
  }
}
