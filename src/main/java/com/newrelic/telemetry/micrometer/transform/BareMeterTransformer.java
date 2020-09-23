/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.transform;

import static java.util.stream.StreamSupport.stream;

import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Meter;
import java.util.Collection;
import java.util.stream.Collectors;

public class BareMeterTransformer {

  private final Clock clock;
  private final AttributesMaker attributesMaker = new AttributesMaker();

  public BareMeterTransformer(Clock clock) {
    this.clock = clock;
  }

  public Collection<Metric> transform(Meter meter) {
    Meter.Id id = meter.getId();
    long now = clock.wallTime();
    return stream(meter.measure().spliterator(), false)
        .filter(measurement -> Double.isFinite(measurement.getValue()))
        .map(
            measurement ->
                new Gauge(
                    id.getName() + "." + measurement.getStatistic().getTagValueRepresentation(),
                    measurement.getValue(),
                    now,
                    attributesMaker.make(id, "meter")))
        .collect(Collectors.toSet());
  }
}
