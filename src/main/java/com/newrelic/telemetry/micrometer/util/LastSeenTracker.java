/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.util;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

// Used to track previous times/values between metric reports
public class LastSeenTracker {

  private final Map<String, LastSeen> lastSeen = new WeakHashMap<>();

  /* Returns the last seen/known value and time for a given metric (by name) */
  public LastSeen getAndUpdate(String name, long now, double value, double duration) {
    LastSeen newLastSeen = new LastSeen(now, value, duration);
    LastSeen lastSeenLastSeen = lastSeen.get(name);
    if (lastSeenLastSeen == null) {
      lastSeenLastSeen = new LastSeen(newLastSeen.wallTime, 0, 0);
    }
    lastSeen.put(name, newLastSeen);
    return lastSeenLastSeen;
  }

  public static class LastSeen {
    private final double value;
    private final long wallTime;
    private final double duration;

    public LastSeen(long wallTime, double value, double duration) {
      this.value = value;
      this.wallTime = wallTime;
      this.duration = duration;
    }

    public double getValue() {
      return value;
    }

    public long getTime() {
      return wallTime;
    }

    public double getDuration() {
      return duration;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      LastSeen that = (LastSeen) o;
      return Double.compare(that.value, value) == 0
          && wallTime == that.wallTime
          && Double.compare(that.duration, duration) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value, wallTime, duration);
    }

    @Override
    public String toString() {
      return "LastSeen{"
          + "value="
          + value
          + ", wallTime="
          + wallTime
          + ", duration="
          + duration
          + '}';
    }
  }
}
