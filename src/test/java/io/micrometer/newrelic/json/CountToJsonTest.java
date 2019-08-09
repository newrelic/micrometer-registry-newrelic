/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Count;
import org.junit.jupiter.api.Test;

class CountToJsonTest {

  @Test
  void testToJson() {
    Attributes attrs = new Attributes().put("joe", "bob").put("other", true);
    Count count = new Count("tre", 99.1, 90210, 90215, attrs);
    String expected =
        "{\"name\":\"tre\",\"type\":\"count\",\"value\":99.1,\"timestamp\":90210,\"interval.ms\":5,\"attributes\":{\"joe\":\"bob\",\"other\":true}}";

    CountToJson marshaller = new CountToJson();

    String result = marshaller.apply(count);
    assertEquals(expected, result);
  }

  @Test
  void testNoAttrs() {
    Count count = new Count("tre", 7, 8, 11, new Attributes());
    String expected =
        "{\"name\":\"tre\",\"type\":\"count\",\"value\":7.0,\"timestamp\":8,\"interval.ms\":3}";

    CountToJson marshaller = new CountToJson();

    String result = marshaller.apply(count);
    assertEquals(expected, result);
  }
}
