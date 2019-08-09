/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;

public class CounterAdapter implements CommonCounterTransformer.CommonCounter {
  private final Counter counter;

  public CounterAdapter(Counter counter) {
    this.counter = counter;
  }

  @Override
  public String sourceType() {
    return "counter";
  }

  @Override
  public Meter.Id id() {
    return counter.getId();
  }

  @Override
  public double count() {
    return counter.count();
  }
}
