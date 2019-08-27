/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.newrelic.telemetry.Attributes;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.noop.NoopGauge;
import org.junit.jupiter.api.Test;

class AttributesMakerTest {

  @Test
  void testAttributes() {
    Tags tags = Tags.of("foo", "bar", "bar", "biz");
    Id id = new Id("jimbo", tags, "mm", "a thinger", Type.GAUGE);
    Meter meter = new NoopGauge(id);
    Attributes expected =
        new Attributes()
            .put("source.type", "gauge")
            .put("baseUnit", "mm")
            .put("description", "a thinger")
            .put("foo", "bar")
            .put("bar", "biz");

    AttributesMaker testClass = new AttributesMaker();
    Attributes result = testClass.make(meter.getId(), "gauge");

    assertEquals(expected, result);
  }

  @Test
  void testAttributes_nullValues() {
    Tags tags = Tags.of("foo", "bar", "bar", "biz");
    Id id = new Id("jimbo", tags, null, null, Type.GAUGE);
    Meter meter = new NoopGauge(id);
    Attributes expected =
        new Attributes().put("source.type", "gauge").put("foo", "bar").put("bar", "biz");

    AttributesMaker testClass = new AttributesMaker();
    Attributes result = testClass.make(meter.getId(), "gauge");

    assertEquals(expected, result);
  }
}
