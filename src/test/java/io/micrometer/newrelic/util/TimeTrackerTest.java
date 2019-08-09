/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Clock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TimeTrackerTest {

  @Test
  @DisplayName("Make sure the right value comes back from a new time tracker")
  void testInitialValue() throws Exception {
    Clock clock = mock(Clock.class);

    long now = System.currentTimeMillis();
    when(clock.wallTime()).thenReturn(now);

    TimeTracker timeTracker = new TimeTracker(clock);

    assertEquals(now, timeTracker.getCurrentTime());
    assertEquals(now, timeTracker.getPreviousTime());
  }

  @Test
  @DisplayName("Make sure the ticker ticks")
  void testTickWorks() throws Exception {
    Clock clock = mock(Clock.class);

    long now = System.currentTimeMillis();
    long later = now + 5000;
    long evenLater = later + 5000;
    when(clock.wallTime()).thenReturn(now, later, evenLater);

    TimeTracker timeTracker = new TimeTracker(clock);

    timeTracker.tick();

    assertEquals(evenLater, timeTracker.getCurrentTime());
    assertEquals(later, timeTracker.getPreviousTime());
  }
}
