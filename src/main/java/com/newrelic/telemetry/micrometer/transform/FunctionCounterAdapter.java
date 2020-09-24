/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.transform;

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Meter;

public class FunctionCounterAdapter implements CommonCounterTransformer.CommonCounter {
  private final FunctionCounter functionCounter;

  public FunctionCounterAdapter(FunctionCounter functionCounter) {
    this.functionCounter = functionCounter;
  }

  @Override
  public String sourceType() {
    return "functionCounter";
  }

  @Override
  public Meter.Id id() {
    return functionCounter.getId();
  }

  @Override
  public double count() {
    return functionCounter.count();
  }
}
