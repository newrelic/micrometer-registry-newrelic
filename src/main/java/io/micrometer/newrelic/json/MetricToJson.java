/*
 * ---------------------------------------------------------------------------------------------
 *  Copyright (c) 2019 New Relic Corporation. All rights reserved.
 *  Licensed under the Apache 2.0 License. See LICENSE in the project root directory for license information.
 * --------------------------------------------------------------------------------------------
 */

package io.micrometer.newrelic.json;

import com.newrelic.telemetry.Count;
import com.newrelic.telemetry.Gauge;
import com.newrelic.telemetry.MetricJsonGenerator;
import com.newrelic.telemetry.Summary;
import java.util.function.Function;

public class MetricToJson implements MetricJsonGenerator {

  private final Function<Summary, String> summaryToJson;
  private final Function<Count, String> countToJson;
  private final Function<Gauge, String> gaugeToJson;

  public MetricToJson() {
    this(new SummaryToJson(), new CountToJson(), new GaugeToJson());
  }

  public MetricToJson(
      Function<Summary, String> summaryToJson,
      Function<Count, String> countToJson,
      Function<Gauge, String> gaugeToJson) {
    this.summaryToJson = summaryToJson;
    this.countToJson = countToJson;
    this.gaugeToJson = gaugeToJson;
  }

  @Override
  public String writeSummaryJson(Summary summary) {
    return summaryToJson.apply(summary);
  }

  @Override
  public String writeGaugeJson(Gauge gauge) {
    return gaugeToJson.apply(gauge);
  }

  @Override
  public String writeCountJson(Count count) {
    return countToJson.apply(count);
  }
}
