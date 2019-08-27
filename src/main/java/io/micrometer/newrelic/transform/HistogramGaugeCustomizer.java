/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import static java.util.Collections.emptyList;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.HistogramGauges;
import io.micrometer.core.instrument.distribution.HistogramSupport;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.util.DoubleFormat;
import java.util.function.Function;

public class HistogramGaugeCustomizer {

  public void registerHistogramGauges(Timer timer, MeterRegistry registry) {
    registerHistogramGauges(timer, percentile -> percentile.value(timer.baseTimeUnit()), registry);
  }

  public void registerHistogramGauges(DistributionSummary summary, MeterRegistry registry) {
    registerHistogramGauges(summary, ValueAtPercentile::value, registry);
  }

  private void registerHistogramGauges(
      HistogramSupport histogramSupport,
      Function<ValueAtPercentile, Double> toDouble,
      MeterRegistry registry) {
    Meter.Id id = histogramSupport.getId();
    HistogramGauges.register(
        histogramSupport,
        registry,
        percentile -> id.getName() + ".percentiles",
        percentile ->
            Tags.concat(
                id.getTagsAsIterable(),
                "newRelic.percentile",
                DoubleFormat.decimalOrNan(percentile.percentile() * 100d)),
        toDouble,
        bucket -> id.getName() + ".histogram",
        // we don't support histograms, so this is moot.
        bucket -> emptyList());
  }
}
