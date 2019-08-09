/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.Summary;
import org.junit.jupiter.api.Test;

class SummaryToJsonTest {

  @Test
  void marshalling() {
    String name = "expert";
    int count = 90210;
    double sum = 44;
    double min = -88;
    double max = 1067.0;
    long start = 1562808480000L;
    long end = start + 5000;
    Attributes attributes = new Attributes().put("flim", "flam").put("tiger", "eye");
    Summary summary = new Summary(name, count, sum, min, max, start, end, attributes);

    String expected =
        "{\"name\":\"expert\",\"type\":\"summary\",\"value\":{\"count\":90210,"
            + "\"sum\":44.0,\"min\":-88.0,\"max\":1067.0},\"timestamp\":1562808480000,\"interval.ms\":5000,"
            + "\"attributes\":{\"tiger\":\"eye\",\"flim\":\"flam\"}}";

    SummaryToJson summaryToJson = new SummaryToJson();
    String result = summaryToJson.apply(summary);

    assertEquals(expected, result);
  }

  @Test
  void testNoAttrs() {
    Summary summary = new Summary("bart", 12, 9, 2, 11, 777L, 888L, new Attributes());

    String expected =
        "{\"name\":\"bart\",\"type\":\"summary\",\"value\":{\"count\":12,"
            + "\"sum\":9.0,\"min\":2.0,\"max\":11.0},\"timestamp\":777,\"interval.ms\":111}";

    SummaryToJson summaryToJson = new SummaryToJson();
    String result = summaryToJson.apply(summary);

    assertEquals(expected, result);
  }
}
