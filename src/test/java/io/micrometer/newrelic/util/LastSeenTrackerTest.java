/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micrometer.newrelic.util.LastSeenTracker.LastSeen;
import org.junit.jupiter.api.Test;

class LastSeenTrackerTest {

  private static final String NAME = "foooo";

  @Test
  void testFirstCallReturnsNowWithZeroValue() {
    LastSeenTracker testClass = new LastSeenTracker();
    long now = System.currentTimeMillis();
    double value = 77.1;
    LastSeen result = testClass.getAndUpdate(NAME, now, value, 17.7);
    assertEquals(new LastSeen(now, 0, 0), result);
  }

  @Test
  void getMultipleCalls() {
    LastSeenTracker testClass = new LastSeenTracker();
    long now = System.currentTimeMillis();
    LastSeen firstResult = testClass.getAndUpdate(NAME, now, 88.8, 17.7);
    LastSeen secondResult = testClass.getAndUpdate(NAME, now + 10, 99.9, 18.7);
    LastSeen thirdResult = testClass.getAndUpdate(NAME, now + 20, 11.1, 19.7);
    assertEquals(new LastSeen(now, 0, 0), firstResult);
    assertEquals(new LastSeen(now, 88.8, 17.7), secondResult);
    assertEquals(new LastSeen(now + 10, 99.9, 18.7), thirdResult);
  }
}
