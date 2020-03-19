### New Relic Micrometer registry
A [Micrometer metrics](https://micrometer.io/) registry for sending dimensional metrics to New Relic using the New Relic Java Telemetry SDK.

This registry should be able to be used both within a Spring Boot application, or any application that uses
micrometer for recording metrics.

Note: the micrometer project currently includes a NewRelicMeterRegistry that writes metrics to NewRelic as 
custom events. This implementation is intended to supersede that, as [dimensional metrics](https://docs.newrelic.com/docs/using-new-relic/data/understand-data/new-relic-data-types#dimensional-metrics) are a better fit for micrometer metrics than custom [events](https://docs.newrelic.com/docs/using-new-relic/data/understand-data/new-relic-data-types#events-new-relic). 

### How to use:

1) via gradle: 
  
    `implementation 'com.newrelic.telemetry:micrometer-registry-new-relic:0.4.0'`

    Note: you might need to also include a dependency on micrometer-core itself, for example:

    `implementation 'io.micrometer:micrometer-core:1.2.2'`
    
2) via maven:

```
    <dependency>
        <groupId>com.newrelic.telemetry</groupId>
        <artifactId>micrometer-registry-new-relic</artifactId>
        <version>0.4.0</version>
    </dependency>
```

3) The registry class is `io.micrometer.newrelic.NewRelicRegistry`. 
It will need to be built via a `io.micrometer.NewRelicRegistryConfig` implementation. An example of how you might do that is in the wiki: [Spring-Config-Example](https://github.com/newrelic/micrometer-registry-newrelic/wiki/Spring-Config-Example)

In order to send metrics to New Relic, you will also need an Insights Insert API Key. Please see [New Relic Api Keys](https://docs.newrelic.com/docs/apis/get-started/intro-apis/types-new-relic-api-keys#) for more information.


And, that's it! For details on what your metrics will look like once they are reported, please see the [Micrometer exporter spec](https://github.com/newrelic/exporter-specs). 

### Building
CI builds are run on Azure Pipelines: 
[![Build Status](https://dev.azure.com/NRAzurePipelines/Java%20CI/_apis/build/status/PR%20build%20for%20Micrometer%20Registry?branchName=master)](https://dev.azure.com/NRAzurePipelines/Java%20CI/_build/latest?definitionId=2&branchName=master)

The project uses gradle 5 for building, and the gradle wrapper is provided.

To compile, run the tests and build the jar:

`$ ./gradlew build`

### Find and use your data

For tips on how to find and query your data, see [Find metric data](https://docs.newrelic.com/docs/data-ingest-apis/get-data-new-relic/metric-api/introduction-metric-api#find-data).

For general querying information, see:
- [Query New Relic data](https://docs.newrelic.com/docs/using-new-relic/data/understand-data/query-new-relic-data)
- [Intro to NRQL](https://docs.newrelic.com/docs/query-data/nrql-new-relic-query-language/getting-started/introduction-nrql)

