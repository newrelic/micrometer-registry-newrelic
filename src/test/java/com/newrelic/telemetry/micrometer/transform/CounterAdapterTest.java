/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package com.newrelic.telemetry.micrometer.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

class CounterAdapterTest {

  @Test
  void testAdapt() throws Exception {
    Counter counter = mock(Counter.class);
    Meter.Id id =
        new Meter.Id(
            "counterizer", Tags.of(Tag.of("foo", "bar")), "cheese", "stuff", Meter.Type.COUNTER);
    when(counter.getId()).thenReturn(id);
    when(counter.count()).thenReturn(33d);

    CommonCounterTransformer.CommonCounter result = new CounterAdapter(counter);
    assertEquals(33d, result.count());
    assertEquals("counter", result.sourceType());
    assertEquals(id, result.id());
  }
}
