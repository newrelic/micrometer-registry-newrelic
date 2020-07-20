/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.micrometer.newrelic;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.metrics.Count;
import com.newrelic.telemetry.metrics.Gauge;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.metrics.Summary;
import io.micrometer.NewRelicRegistryConfig;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.newrelic.transform.BareMeterTransformer;
import io.micrometer.newrelic.transform.CommonCounterTransformer;
import io.micrometer.newrelic.transform.DistributionSummaryTransformer;
import io.micrometer.newrelic.transform.FunctionTimerTransformer;
import io.micrometer.newrelic.transform.GaugeTransformer;
import io.micrometer.newrelic.transform.HistogramGaugeCustomizer;
import io.micrometer.newrelic.transform.LongTaskTimerTransformer;
import io.micrometer.newrelic.transform.TimeGaugeTransformer;
import io.micrometer.newrelic.transform.TimerTransformer;
import io.micrometer.newrelic.util.TimeTracker;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NewRelicRegistryTest {

  @Mock(lenient = true)
  private NewRelicRegistryConfig config;

  @Mock private Clock clock;
  @Mock private TimeGaugeTransformer timeGaugeTransformer;
  @Mock private GaugeTransformer gaugeTransformer;
  @Mock private TimerTransformer timerTransformer;
  @Mock private FunctionTimerTransformer functionTimerTransformer;
  @Mock private CommonCounterTransformer<Counter> counterTransformer;
  @Mock private LongTaskTimerTransformer longTaskTimerTransformer;
  @Mock private CommonCounterTransformer<FunctionCounter> functionCounterTransformer;
  @Mock private TelemetryClient telemetryClient;
  @Mock private DistributionSummaryTransformer distributionSummaryTransformer;
  @Mock private TimeTracker timeTracker;
  @Mock private BareMeterTransformer bareMeterTransformer;
  @Mock private HistogramGaugeCustomizer histogramCustomizer;
  private Attributes commonAttributes;
  private Attributes expectedAttributes;
  private NewRelicRegistry newRelicRegistry;

  @BeforeEach
  void setUp() {
    commonAttributes = new Attributes();

    expectedAttributes =
        new Attributes()
            .put("instrumentation.provider", "micrometer")
            .put("collector.name", "micrometer-registry-newrelic")
            .put("collector.version", "UnknownVersion")
            .put("service.name", "my awesome service");
    when(config.batchSize()).thenReturn(10);
    when(config.step()).thenReturn(Duration.ofDays(1));
    when(config.serviceName()).thenReturn("my awesome service");
    newRelicRegistry =
        new NewRelicRegistry(
            config,
            clock,
            commonAttributes,
            telemetryClient,
            timeGaugeTransformer,
            gaugeTransformer,
            timerTransformer,
            functionTimerTransformer,
            counterTransformer,
            longTaskTimerTransformer,
            functionCounterTransformer,
            distributionSummaryTransformer,
            bareMeterTransformer,
            histogramCustomizer,
            timeTracker);
  }

  @Test
  @DisplayName("publishing a time gauge works")
  void testPublishTimeGauge() {
    Gauge expectedGauge =
        new Gauge("timeGauge", 5d, System.currentTimeMillis(), new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedGauge), expectedAttributes);

    when(timeGaugeTransformer.transform(isA(TimeGauge.class))).thenReturn(expectedGauge);

    newRelicRegistry
        .more()
        .timeGauge(
            "timeGauge",
            singletonList(Tag.of("foo", "bar")),
            "cheese",
            TimeUnit.MICROSECONDS,
            value -> 5);

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  @DisplayName("publishing a gauge works")
  void testPublishGauge() {
    Gauge expectedGauge =
        new Gauge("gauge", 5d, System.currentTimeMillis(), new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedGauge), expectedAttributes);

    when(gaugeTransformer.transform(isA(io.micrometer.core.instrument.Gauge.class)))
        .thenReturn(expectedGauge);

    newRelicRegistry.gauge("gauge", singletonList(Tag.of("foo", "bar")), 5);

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  @DisplayName("service name can be set at the top level")
  void testServiceName() {
    Gauge expectedGauge =
        new Gauge("gauge", 5d, System.currentTimeMillis(), new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch =
        new MetricBatch(
            singletonList(expectedGauge),
            expectedAttributes.put("service.name", "my awesome service"));

    when(gaugeTransformer.transform(isA(io.micrometer.core.instrument.Gauge.class)))
        .thenReturn(expectedGauge);

    newRelicRegistry.gauge("gauge", singletonList(Tag.of("foo", "bar")), 5);

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  @DisplayName("publishing a timer works")
  void testPublishTimer() {
    long now = System.currentTimeMillis();
    Summary expectedMetric =
        new Summary(
            "tire", 55, 122d, 44d, 99d, now - 1500, now, new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedMetric), expectedAttributes);

    Timer timer = newRelicRegistry.timer("timer", singletonList(Tag.of("foo", "bar")));
    when(timerTransformer.transform(timer)).thenReturn(singleton(expectedMetric));

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  @DisplayName("publishing a function timer works")
  void testFunctionTimer() {
    long now = System.currentTimeMillis();
    Summary expectedMetric =
        new Summary(
            "tire", 55, 122d, 44d, 99d, now - 1500, now, new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedMetric), expectedAttributes);

    when(functionTimerTransformer.transform(isA(FunctionTimer.class)))
        .thenReturn(singleton(expectedMetric));

    newRelicRegistry
        .more()
        .timer(
            "function timer",
            singletonList(Tag.of("foo", "bar")),
            "",
            x -> 13L,
            x -> 44.0,
            TimeUnit.MILLISECONDS);

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  @DisplayName("publishing a counter works")
  void testCounter() {
    long now = System.currentTimeMillis();
    Count expectedMetric =
        new Count("dracula", 33, now - 15000, now, new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedMetric), expectedAttributes);

    when(counterTransformer.transform(isA(Counter.class))).thenReturn(expectedMetric);

    newRelicRegistry.counter("dracula", singletonList(Tag.of("foo", "bar")));

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  @DisplayName("publishing a long task timer works")
  void testLongTaskTimer() {
    long now = System.currentTimeMillis();
    Summary expectedMetric =
        new Summary(
            "tire", 55, 122d, 44d, 99d, now - 1500, now, new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedMetric), expectedAttributes);

    when(longTaskTimerTransformer.transform(isA(LongTaskTimer.class)))
        .thenReturn(Collections.singleton(expectedMetric));

    newRelicRegistry.more().longTaskTimer("tire", singletonList(Tag.of("foo", "bar")));

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  @DisplayName("publishing a function counter works")
  void testFunctionCounter() {
    long now = System.currentTimeMillis();
    Count expectedMetric =
        new Count("dracula", 11.33, now - 15000, now, new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedMetric), expectedAttributes);

    when(functionCounterTransformer.transform(isA(FunctionCounter.class)))
        .thenReturn(expectedMetric);

    newRelicRegistry.more().counter("dracula", singletonList(Tag.of("foo", "bar")), 77.1);

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  @DisplayName("publishing a distribution summary works")
  void testDistributionSummary() {
    long now = System.currentTimeMillis();
    Metric expectedMetric =
        new Count("quick", 11.33, now - 15000, now, new Attributes().put("foo", "bar"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedMetric), expectedAttributes);

    when(distributionSummaryTransformer.transform(isA(DistributionSummary.class)))
        .thenReturn(singleton(expectedMetric));

    newRelicRegistry.summary("quick", singletonList(Tag.of("foo", "bar")));

    newRelicRegistry.publish();

    verify(telemetryClient).sendBatch(expectedBatch);
    verify(timeTracker).tick();
  }

  @Test
  void testMetricFiltersAreSupported() {
    long now = System.currentTimeMillis();
    Gauge expectedMetric = new Gauge("8_oh_eight", 34, now, new Attributes().put("a", "b"));
    MetricBatch expectedBatch = new MetricBatch(singletonList(expectedMetric), expectedAttributes);

    when(gaugeTransformer.transform(isA(io.micrometer.core.instrument.Gauge.class)))
        .thenReturn(expectedMetric);

    newRelicRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("7"));
    newRelicRegistry.gauge("7_eleven", 33);
    newRelicRegistry.gauge("8_oh_eight", 33);
    newRelicRegistry.publish();
    verify(telemetryClient).sendBatch(expectedBatch);
  }

  @Test
  @DisplayName("closing telemetryClient's executor")
  void testClose() {
    newRelicRegistry.close();
    verify(telemetryClient).shutdown();
  }
}
