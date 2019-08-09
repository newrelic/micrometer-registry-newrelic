/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.util;

import io.micrometer.core.instrument.Clock;
import java.util.concurrent.atomic.AtomicLong;

public class TimeTracker {

  private final Clock clock;
  private final AtomicLong previousTime;

  public TimeTracker(Clock clock) {
    this.clock = clock;
    this.previousTime = new AtomicLong(clock.wallTime());
  }

  // call this at the end of the harvest/report
  public void tick() {
    previousTime.set(clock.wallTime());
  }

  public long getCurrentTime() {
    return clock.wallTime();
  }

  public long getPreviousTime() {
    return previousTime.get();
  }
}
