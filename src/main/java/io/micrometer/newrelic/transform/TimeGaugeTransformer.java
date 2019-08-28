/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import com.newrelic.telemetry.Metric;
import io.micrometer.core.instrument.TimeGauge;

public class TimeGaugeTransformer {

  private final GaugeTransformer delegate;

  public TimeGaugeTransformer(GaugeTransformer delegate) {
    this.delegate = delegate;
  }

  public Metric transform(TimeGauge timeGauge) {
    return delegate.transform(timeGauge, "timeGauge");
  }
}
