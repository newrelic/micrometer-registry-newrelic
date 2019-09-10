### New Relic Micrometer Registry
A [Micrometer metrics](https://micrometer.io/) registry for sending dimensional metrics to New Relic using the New Relic Java Telemetry SDK.

This registry should be able to be used both within a Spring Boot application, or any application that uses
micrometer for recording metrics.

Note: the micrometer project currently includes a NewRelicMeterRegistry that writes metrics to NewRelic as 
Custom Events. This implementation is intended to supersede that, as dimensional metrics are a better fit
for micrometer metrics than Custom Events.

### How to use:

1) via gradle: 
  
    `implementation 'com.newrelic.telemetry:micrometer-registry-new-relic:0.2.0'`

    Note: you might need to also include a dependency on micrometer-core itself, for example:

    `implementation 'io.micrometer:micrometer-core:1.2.0'`
    
2) via maven:

```
    <dependency>
        <groupId>com.newrelic.telemetry</groupId>
        <artifactId>micrometer-registry-new-relic</artifactId>
        <version>0.2.0</version>
    </dependency>
```

3) The registry class is `io.micrometer.newrelic.NewRelicRegistry`. 
It will need to be built via a `io.micrometer.NewRelicRegistryConfig` implementation.

An example of how you might do that is in the wiki: [Spring-Config-Example](https://github.com/newrelic/micrometer-registry-newrelic/wiki/Spring-Config-Example)


And, that's it! For details on what your metrics will look like once they are reported, please see the [Micrometer exporter spec](https://github.com/newrelic/exporter-specs). 
