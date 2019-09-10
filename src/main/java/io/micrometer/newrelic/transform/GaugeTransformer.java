/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import com.newrelic.telemetry.metrics.Gauge;
import io.micrometer.core.instrument.Clock;

public class GaugeTransformer {

  private final Clock clock;
  private final AttributesMaker attributesMaker;

  public GaugeTransformer(Clock clock, AttributesMaker attributesMaker) {
    this.clock = clock;
    this.attributesMaker = attributesMaker;
  }

  public Gauge transform(io.micrometer.core.instrument.Gauge gauge) {
    return transform(gauge, "gauge");
  }

  Gauge transform(io.micrometer.core.instrument.Gauge gauge, String sourceType) {
    return new Gauge(
        gauge.getId().getName(),
        gauge.value(),
        clock.wallTime(),
        attributesMaker.make(gauge.getId(), sourceType));
  }
}
