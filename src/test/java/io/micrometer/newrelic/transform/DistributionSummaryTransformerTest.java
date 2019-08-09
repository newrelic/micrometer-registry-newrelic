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
import com.newrelic.telemetry.Count;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Metric;
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

    Metric expectedCount =
        new Count(metricName + ".count", snapshotCount, before, now, summaryAttributes);
    Metric expectedTotal = new Gauge(metricName + ".total", snapshotTotal, now, summaryAttributes);
    Metric expectedMax = new Gauge(metricName + ".max", snapshotMax, now, summaryAttributes);

    Metric expectedPercentile1 =
        new Gauge(
            metricName + ".percentiles",
            valuePercentiles[0].value(),
            now,
            new Attributes()
                .put("source_type", "distribution_summary")
                .put("nr_percentile", valuePercentiles[0].percentile() * 100d));
    Metric expectedPercentile2 =
        new Gauge(
            metricName + ".percentiles",
            valuePercentiles[1].value(),
            now,
            new Attributes()
                .put("source_type", "distribution_summary")
                .put("nr_percentile", valuePercentiles[1].percentile() * 100d));

    Collection<Metric> expected =
        Arrays.asList(
            expectedCount, expectedTotal, expectedMax, expectedPercentile1, expectedPercentile2);

    TimeTracker timeTracker = mock(TimeTracker.class);
    AttributesMaker attributesMaker = mock(AttributesMaker.class);

    HistogramSnapshot snapshot =
        new HistogramSnapshot(
            snapshotCount, snapshotTotal, snapshotMax, valuePercentiles, null, null);

    when(timeTracker.getCurrentTime()).thenReturn(now);
    when(timeTracker.getPreviousTime()).thenReturn(before);
    when(attributesMaker.make(summaryId, "distribution_summary")).thenReturn(summaryAttributes);
    when(summary.getId()).thenReturn(summaryId);
    when(summary.takeSnapshot()).thenReturn(snapshot);

    DistributionSummaryTransformer testClass =
        new DistributionSummaryTransformer(timeTracker, attributesMaker);
    Collection<Metric> result = testClass.transform(summary);
    assertEquals(expected, result);
  }
}
