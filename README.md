### New Relic Micrometer registry
A [Micrometer metrics](https://micrometer.io/) registry for sending dimensional metrics to New Relic using the New Relic Java Telemetry SDK.

This registry should be able to be used both within a Spring Boot application, or any application that uses
micrometer for recording metrics.

Note: the micrometer project currently includes a NewRelicMeterRegistry that writes metrics to NewRelic as 
custom events. This implementation is intended to supersede that, as [dimensional metrics](https://docs.newrelic.com/docs/using-new-relic/data/understand-data/new-relic-data-types#dimensional-metrics) are a better fit for micrometer metrics than custom [events](https://docs.newrelic.com/docs/using-new-relic/data/understand-data/new-relic-data-types#events-new-relic). 

### Usage:

1) via gradle: 
  
    `implementation 'com.newrelic.telemetry:micrometer-registry-new-relic:0.5.0'`

    Note: you might need to also include a dependency on micrometer-core itself, for example:

    `implementation 'io.micrometer:micrometer-core:1.5.4'`
    
2) via maven:

```
    <dependency>
        <groupId>com.newrelic.telemetry</groupId>
        <artifactId>micrometer-registry-new-relic</artifactId>
        <version>0.5.0</version>
    </dependency>
```

3) The registry class is `io.micrometer.newrelic.NewRelicRegistry`. 
It will need to be built via a `io.micrometer.NewRelicRegistryConfig` implementation. An example of how you might do that is in the wiki: [Spring-Config-Example](https://github.com/newrelic/micrometer-registry-newrelic/wiki/Spring-Config-Example)

In order to send metrics to New Relic, you will also need an Insert API Key. Please see [New Relic Api Keys](https://docs.newrelic.com/docs/apis/get-started/intro-apis/types-new-relic-api-keys#) for more information.

And, that's it! For details on what your metrics will look like once they are reported, please see the [Micrometer exporter spec](https://github.com/newrelic/exporter-specs). 

### Javadoc for this project can be found here: [![Javadocs][javadoc-image]][javadoc-url]

### Building
CI builds are run on Azure Pipelines: 
[![Build Status](https://dev.azure.com/NRAzurePipelines/Java%20CI/_apis/build/status/PR%20build%20for%20Micrometer%20Registry?branchName=main)](https://dev.azure.com/NRAzurePipelines/Java%20CI/_build/latest?definitionId=2&branchName=main)

The project uses gradle 5 for building, and the gradle wrapper is provided.

To compile, run the tests and build the jar:

`$ ./gradlew build`

### Find and use your data

For tips on how to find and query your data, see [Find metric data](https://docs.newrelic.com/docs/data-ingest-apis/get-data-new-relic/metric-api/introduction-metric-api#find-data).

For general querying information, see:
- [Query New Relic data](https://docs.newrelic.com/docs/using-new-relic/data/understand-data/query-new-relic-data)
- [Intro to NRQL](https://docs.newrelic.com/docs/query-data/nrql-new-relic-query-language/getting-started/introduction-nrql)

### Release Process

#### Publish to Staging Repo

To stage the release simply submit and merge a PR to update the [build.gradle.kts](build.gradle.kts) file with the version to be released (e.g. `version := "0.5.0"`).

Results of the job can be viewed here: https://dev.azure.com/NRAzurePipelines/Java%20CI/_build

After the staging release job has run successfully it will publish the new artifact to a staging repository on Sonatype at: https://oss.sonatype.org/#stagingRepositories.

#### Manually Release Staging Repo

1. Find the staging repo on Sonatype, which should be named similar to `comnewrelic-nnnn`, and validate that the contents and version look correct.
2. If the contents look correct, select the staging repo and choose `close`, leaving a comment such as `releasing 0.5.0`.
3. When the staging repo is finished closing, select the staging repo and choose `release`, keeping the `Automatically Drop` checkbox checked, and leave a comment such as `releasing 0.5.0`.
4. Verify that the artifact was published on Maven Central at: https://repo1.maven.org/maven2/com/newrelic/telemetry/micrometer-registry-new-relic/

#### Post Release

Submit and merge a PR with the following:
* Update the [build.gradle.kts](build.gradle.kts) file with to a snapshot version of a potential future release (e.g. `version  := "0.5.1-SNAPSHOT"`).
* Update the [CHANGELOG](CHANGELOG.md) with details of the new release:
  ```markdown
  ## [0.5.0]
  - Miscellaneous bug fixes and tweaks
  ```
* Update the [Usage](#usage) example in the [README](README.md) with the newly released version (e.g. `implementation 'com.newrelic.telemetry:micrometer-registry-new-relic:0.5.0'`).

[javadoc-image]: https://www.javadoc.io/badge/com.newrelic.telemetry/micrometer-registry-new-relic.svg
[javadoc-url]: https://www.javadoc.io/doc/com.newrelic.telemetry/micrometer-registry-new-relic