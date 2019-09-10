/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.newrelic.util.TimeTracker;
import java.util.function.Function;

public class CommonCounterTransformer<T> {

  private final TimeTracker timeTracker;
  private final AttributesMaker attributesMaker;
  private final Function<T, CommonCounter> adapter;

  public CommonCounterTransformer(
      TimeTracker timeTracker,
      AttributesMaker attributesMaker,
      Function<T, CommonCounter> adapter) {
    this.timeTracker = timeTracker;
    this.attributesMaker = attributesMaker;
    this.adapter = adapter;
  }

  public Count transform(T micrometerCounter) {
    CommonCounter counter = adapter.apply(micrometerCounter);
    Attributes attributes = attributesMaker.make(counter.id(), counter.sourceType());
    long now = timeTracker.getCurrentTime();
    String name = counter.id().getName();
    return new Count(name, counter.count(), timeTracker.getPreviousTime(), now, attributes);
  }

  /** This provides a shim between the micrometer counter types, giving them a common interface. */
  public interface CommonCounter {

    String sourceType();

    Id id();

    double count();
  }
}
