/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import static java.util.stream.Collectors.toList;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Count;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.Metric;
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
    Stream<Metric> summaryMetrics = buildBaseMetrics(distributionSummary, now, snapshot);
    Stream<Metric> percentileMetrics = buildPercentiles(distributionSummary, now, snapshot);

    return Stream.concat(summaryMetrics, percentileMetrics).collect(toList());
  }

  private Stream<Metric> buildBaseMetrics(
      DistributionSummary distributionSummary, long now, HistogramSnapshot snapshot) {
    Attributes attributes =
        attributesMaker.make(distributionSummary.getId(), "distributionSummary");
    String name = distributionSummary.getId().getName();
    return Stream.of(
        new Count(
            name + ".count",
            (int) snapshot.count(),
            timeTracker.getPreviousTime(),
            now,
            attributes),
        new Gauge(name + ".total", snapshot.total(), now, attributes),
        new Gauge(name + ".max", snapshot.max(), now, attributes));
  }

  private Stream<Metric> buildPercentiles(
      DistributionSummary distributionSummary, long now, HistogramSnapshot snapshot) {
    return Arrays.stream(snapshot.percentileValues())
        .map(
            valueAtPercentile ->
                new Gauge(
                    distributionSummary.getId().getName() + ".percentiles",
                    valueAtPercentile.value(),
                    now,
                    new Attributes()
                        .put("source.type", "distributionSummary")
                        .put("newRelic.percentile", valueAtPercentile.percentile() * 100d)));
  }
}
