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

import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Test;

class FunctionCounterAdapterTest {

  @Test
  void testAdapt() throws Exception {
    FunctionCounter counter = mock(FunctionCounter.class);
    Meter.Id id =
        new Meter.Id(
            "counterizer", Tags.of(Tag.of("foo", "bar")), "cheese", "stuff", Meter.Type.COUNTER);
    when(counter.getId()).thenReturn(id);
    when(counter.count()).thenReturn(33d);

    CommonCounterTransformer.CommonCounter result = new FunctionCounterAdapter(counter);
    assertEquals(33d, result.count());
    assertEquals("functionCounter", result.sourceType());
    assertEquals(id, result.id());
  }
}
