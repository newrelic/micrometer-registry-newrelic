/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.micrometer.newrelic.transform;

import static java.util.stream.Collectors.toList;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.Summary;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.newrelic.util.TimeTracker;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class DistributionSummaryTransformer {

  private final AttributesMaker attributesMaker;
  private final TimeTracker timeTracker;

  public DistributionSummaryTransformer(TimeTracker timeTracker, AttributesMaker attributesMaker) {
    this.attributesMaker = attributesMaker;
    this.timeTracker = timeTracker;
  }

  public Collection<Metric> transform(DistributionSummary distributionSummary) {
    long now = timeTracker.getCurrentTime();

    HistogramSnapshot snapshot = distributionSummary.takeSnapshot();
    Stream<Metric> summaryMetrics = buildSummaryMetric(distributionSummary, now, snapshot);
    Stream<Metric> percentileMetrics = buildPercentiles(distributionSummary, now, snapshot);

    return Stream.concat(summaryMetrics, percentileMetrics).collect(toList());
  }

  private Stream<Metric> buildSummaryMetric(
      DistributionSummary distributionSummary, long now, HistogramSnapshot snapshot) {
    Attributes attributes =
        attributesMaker.make(distributionSummary.getId(), "distributionSummary");
    String name = distributionSummary.getId().getName();
    return Stream.of(
        new Summary(
            name + ".summary",
            (int) snapshot.count(),
            snapshot.total(),
            Double.NaN,
            snapshot.max(),
            timeTracker.getPreviousTime(),
            now,
            attributes));
  }

  // todo: is this redundant with the gauges being produced by the HistogramGaugeCustomizer? If so,
  // we should remove these.
  private Stream<Metric> buildPercentiles(
      DistributionSummary distributionSummary, long now, HistogramSnapshot snapshot) {
    return Arrays.stream(snapshot.percentileValues())
        .map(
            valueAtPercentile ->
                new Gauge(
                    distributionSummary.getId().getName() + ".percentiles",
                    valueAtPercentile.value(),
                    now,
                    new Attributes().put("percentile", valueAtPercentile.percentile() * 100d)));
  }
}
