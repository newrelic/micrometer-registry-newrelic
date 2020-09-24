/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.micrometer.transform.CommonCounterTransformer.CommonCounter;
import com.newrelic.telemetry.micrometer.util.TimeTracker;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommonCounterTransformerTest {

  @Mock private AttributesMaker attributesMaker;
  @Mock private TimeTracker timeTracker;
  private final String counterName = "a_wonderful_counter";
  private long now;
  private CommonCounterTransformer<String> testClass;
  private Attributes expectedAttrs;
  private CommonCounter counter;

  @BeforeEach
  void setup() {
    now = System.currentTimeMillis();

    expectedAttrs = new Attributes().put("type", "counter").put("foo", "bar");
    String baseUnit = "bagels";
    Tags tags = Tags.of("key1", "value1", "key2", "value2");
    Meter.Id id = new Meter.Id(counterName, tags, baseUnit, "this only counts up", Type.COUNTER);
    counter =
        new CommonCounter() {
          @Override
          public String sourceType() {
            return "shazbot";
          }

          @Override
          public Id id() {
            return id;
          }

          @Override
          public double count() {
            return 99.1;
          }
        };

    when(timeTracker.getCurrentTime()).thenReturn(now);
    when(timeTracker.getPreviousTime()).thenReturn(now - 50);

    testClass = new CommonCounterTransformer<>(timeTracker, attributesMaker, c -> counter);
  }

  @Test
  void transform() {
    Count expectedCount = new Count(counterName, 99.1d, now - 50, now, expectedAttrs);

    when(attributesMaker.make(counter.id(), "shazbot")).thenReturn(expectedAttrs);

    Count result = testClass.transform("data");

    assertEquals(expectedCount, result);
  }
}
