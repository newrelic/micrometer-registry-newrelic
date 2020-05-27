/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.micrometer.newrelic;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.TelemetryClient;
import com.newrelic.telemetry.metrics.Metric;
import com.newrelic.telemetry.metrics.MetricBatch;
import com.newrelic.telemetry.metrics.MetricBatchSender;
import com.newrelic.telemetry.metrics.MetricBatchSenderBuilder;
import io.micrometer.NewRelicRegistryConfig;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.step.StepDistributionSummary;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepTimer;
import io.micrometer.core.instrument.util.MeterPartition;
import io.micrometer.core.ipc.http.HttpSender;
import io.micrometer.core.ipc.http.HttpUrlConnectionSender;
import io.micrometer.newrelic.transform.AttributesMaker;
import io.micrometer.newrelic.transform.BareMeterTransformer;
import io.micrometer.newrelic.transform.CommonCounterTransformer;
import io.micrometer.newrelic.transform.CounterAdapter;
import io.micrometer.newrelic.transform.DistributionSummaryTransformer;
import io.micrometer.newrelic.transform.FunctionCounterAdapter;
import io.micrometer.newrelic.transform.FunctionTimerTransformer;
import io.micrometer.newrelic.transform.GaugeTransformer;
import io.micrometer.newrelic.transform.HistogramGaugeCustomizer;
import io.micrometer.newrelic.transform.LongTaskTimerTransformer;
import io.micrometer.newrelic.transform.TimeGaugeTransformer;
import io.micrometer.newrelic.transform.TimerTransformer;
import io.micrometer.newrelic.util.TimeTracker;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewRelicRegistry extends StepMeterRegistry {

  private static final Logger LOG = LoggerFactory.getLogger(NewRelicRegistry.class);
  private static final String implementationVersion;

  private final NewRelicRegistryConfig config;
  private final TelemetryClient telemetryClient;
  private final Attributes commonAttributes;
  private final TimeGaugeTransformer timeGaugeTransformer;
  private final GaugeTransformer gaugeTransformer;
  private final TimerTransformer timerTransformer;

  private final FunctionTimerTransformer functionTimerTransformer;
  private final CommonCounterTransformer<Counter> counterTransformer;
  private final LongTaskTimerTransformer longTaskTimerTransformer;
  private final CommonCounterTransformer<FunctionCounter> functionCounterTransformer;
  private final DistributionSummaryTransformer distributionSummaryTransformer;
  private final BareMeterTransformer bareMeterTransformer;
  private final TimeTracker timeTracker;
  private final HistogramGaugeCustomizer histogramCustomizer;

  static {
    Package thisPackage = NewRelicRegistry.class.getPackage();
    implementationVersion =
        Optional.ofNullable(thisPackage.getImplementationVersion()).orElse("UnknownVersion");
  }

  // visible for testing
  private NewRelicRegistry(
      NewRelicRegistryConfig config,
      Clock clock,
      Attributes commonAttributes,
      AttributesMaker attributesMaker,
      TimeTracker timeTracker,
      MetricBatchSender metricBatchSender) {
    this(
        config,
        clock,
        commonAttributes,
        new TelemetryClient(metricBatchSender, null),
        new TimeGaugeTransformer(new GaugeTransformer(clock, attributesMaker)),
        new GaugeTransformer(clock, attributesMaker),
        new TimerTransformer(timeTracker),
        new FunctionTimerTransformer(timeTracker),
        new CommonCounterTransformer<>(timeTracker, attributesMaker, CounterAdapter::new),
        new LongTaskTimerTransformer(clock),
        new CommonCounterTransformer<>(timeTracker, attributesMaker, FunctionCounterAdapter::new),
        new DistributionSummaryTransformer(timeTracker, attributesMaker),
        new BareMeterTransformer(clock),
        new HistogramGaugeCustomizer(),
        timeTracker);
  }

  NewRelicRegistry(
      NewRelicRegistryConfig config,
      Clock clock,
      Attributes commonAttributes,
      TelemetryClient telemetryClient,
      TimeGaugeTransformer timeGaugeTransformer,
      GaugeTransformer gaugeTransformer,
      TimerTransformer timerTransformer,
      FunctionTimerTransformer functionTimerTransformer,
      CommonCounterTransformer<Counter> counterTransformer,
      LongTaskTimerTransformer longTaskTimerTransformer,
      CommonCounterTransformer<FunctionCounter> functionCounterTransformer,
      DistributionSummaryTransformer distributionSummaryTransformer,
      BareMeterTransformer bareMeterTransformer,
      HistogramGaugeCustomizer histogramCustomizer,
      TimeTracker timeTracker) {
    super(config, clock);
    this.config = config;
    this.commonAttributes =
        commonAttributes
            .copy()
            .put("instrumentation.provider", "micrometer")
            .put("collector.name", "micrometer-registry-newrelic")
            .put("collector.version", implementationVersion);
    if (config.serviceName() != null) {
      this.commonAttributes.put("service.name", config.serviceName());
    }
    this.telemetryClient = telemetryClient;
    this.timeGaugeTransformer = timeGaugeTransformer;
    this.gaugeTransformer = gaugeTransformer;
    this.timerTransformer = timerTransformer;
    this.functionTimerTransformer = functionTimerTransformer;
    this.counterTransformer = counterTransformer;
    this.longTaskTimerTransformer = longTaskTimerTransformer;
    this.functionCounterTransformer = functionCounterTransformer;
    this.distributionSummaryTransformer = distributionSummaryTransformer;
    this.bareMeterTransformer = bareMeterTransformer;
    this.timeTracker = timeTracker;
    this.histogramCustomizer = histogramCustomizer;
  }

  @Override
  public void start(ThreadFactory threadFactory) {
    LOG.info("New Relic Registry: Version " + implementationVersion + " is starting");
    super.start(threadFactory);
  }

  @Override
  public void close() {
    super.close();
    this.telemetryClient.shutdown();
  }

  @Override
  protected void publish() {
    List<List<Meter>> partitionedData = MeterPartition.partition(this, config.batchSize());
    for (List<Meter> batch : partitionedData) {
      List<Metric> metrics = new ArrayList<>();
      batch.forEach(
          meter -> {
            if (meter instanceof TimeGauge) {
              metrics.add(timeGaugeTransformer.transform((TimeGauge) meter));
            } else if (meter instanceof Gauge) {
              metrics.add(gaugeTransformer.transform((Gauge) meter));
            } else if (meter instanceof Timer) {
              metrics.addAll(timerTransformer.transform((Timer) meter));
            } else if (meter instanceof FunctionTimer) {
              metrics.addAll(functionTimerTransformer.transform((FunctionTimer) meter));
            } else if (meter instanceof Counter) {
              metrics.add(counterTransformer.transform((Counter) meter));
            } else if (meter instanceof DistributionSummary) {
              metrics.addAll(distributionSummaryTransformer.transform((DistributionSummary) meter));
            } else if (meter instanceof LongTaskTimer) {
              metrics.addAll(longTaskTimerTransformer.transform((LongTaskTimer) meter));
            } else if (meter instanceof FunctionCounter) {
              metrics.add(functionCounterTransformer.transform((FunctionCounter) meter));
            } else {
              metrics.addAll(bareMeterTransformer.transform(meter));
            }
          });
      telemetryClient.sendBatch(new MetricBatch(metrics, commonAttributes));
    }
    timeTracker.tick();
  }

  @Override
  protected TimeUnit getBaseTimeUnit() {
    return TimeUnit.MILLISECONDS;
  }

  public static NewRelicRegistryBuilder builder(NewRelicRegistryConfig config) {
    return new NewRelicRegistryBuilder(config);
  }

  @Override
  protected Timer newTimer(
      Meter.Id id,
      DistributionStatisticConfig distributionStatisticConfig,
      PauseDetector pauseDetector) {
    Timer timer =
        new StepTimer(
            id,
            clock,
            distributionStatisticConfig,
            pauseDetector,
            getBaseTimeUnit(),
            this.config.step().toMillis(),
            false);
    histogramCustomizer.registerHistogramGauges(timer, this);
    return timer;
  }

  @Override
  protected DistributionSummary newDistributionSummary(
      Meter.Id id, DistributionStatisticConfig distributionStatisticConfig, double scale) {
    DistributionSummary summary =
        new StepDistributionSummary(
            id, clock, distributionStatisticConfig, scale, config.step().toMillis(), false);
    histogramCustomizer.registerHistogramGauges(summary, this);
    return summary;
  }

  public static class NewRelicRegistryBuilder {

    private NewRelicRegistryConfig config;
    private HttpSender httpSender = new HttpUrlConnectionSender();
    private Attributes commonAttributes = new Attributes();

    public NewRelicRegistryBuilder(NewRelicRegistryConfig config) {
      this.config = config;
    }

    public NewRelicRegistryBuilder httpSender(HttpSender httpSender) {
      this.httpSender = httpSender;
      return this;
    }

    /** Supply a set of attributes that should be applied to all metrics. */
    public NewRelicRegistryBuilder commonAttributes(Attributes commonAttributes) {
      this.commonAttributes = commonAttributes;
      return this;
    }

    public NewRelicRegistry build() {
      MetricBatchSender metricBatchSender = createMetricBatchSender();
      return new NewRelicRegistry(
          config,
          Clock.SYSTEM,
          commonAttributes,
          new AttributesMaker(),
          new TimeTracker(Clock.SYSTEM),
          metricBatchSender);
    }

    private MetricBatchSender createMetricBatchSender() {
      MetricBatchSenderBuilder metricBatchSenderBuilder =
          MetricBatchSender.builder()
              .apiKey(config.apiKey())
              .httpPoster(new MicrometerHttpPoster(httpSender))
              .secondaryUserAgent("NewRelic-Micrometer-Exporter", implementationVersion);
      if (config.enableAuditMode()) {
        metricBatchSenderBuilder.enableAuditLogging();
      }
      if (config.uri() != null) {
        try {
          metricBatchSenderBuilder.uriOverride(URI.create(config.uri()));
        } catch (MalformedURLException e) {
          throw new RuntimeException("Invalid URI for the metric API : " + config.uri(), e);
        }
      }
      return metricBatchSenderBuilder.build();
    }
  }
}
