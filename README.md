### New Relic Micrometer Registry
A [Micrometer metrics](https://micrometer.io/) registry for sending dimensional metrics to New Relic using the New Relic Java Telemetry SDK.

This registry should be able to be used both within a Spring Boot application, or any application that uses
micrometer for recording metrics.

Note: the micrometer project currently includes a NewRelicMeterRegistry that writes metrics to NewRelic as 
Custom Events. This implementation is intended to supersede that, as dimensional metrics are a better fit
for micrometer metrics than Custom Events.

### How to use:

1) via gradle: 
  
    `implementation 'com.newrelic.telemetry:micrometer-registry-new-relic:0.1.1'`

    Note: you might need to also include a dependency on micrometer-core itself, for example:

    `implementation 'io.micrometer:micrometer-core:1.2.0'`

2) The registry class is `io.micrometer.newrelic.NewRelicRegistry`. 
It will need to be built via a `io.micrometer.NewRelicRegistryConfig` implementation.

Here is an example of how you might do that:

```java
        Attributes commonAttributes = new Attributes()
                .put("host", InetAddress.getLocalHost().getHostName())
                .put("appName", "My App Name");

        NewRelicRegistryConfig config = new NewRelicRegistryConfig() {

            @Override
            public String apiKey() {
                //provide an insights insert API key here.
                return System.getenv("NEWRELIC_API_KEY");
            }

            @Override
            public String uri() {
                return "contact open-instrumentation@newrelic.com for the metric api endpoint";
            }

            @Override
            public String get(String key) {
                //required for the interface, but null seems to be correct for this use-case.
                return null;
            }

            @Override
            public Duration step() {
                //how often the registry should report metric data to New Relic.
                return Duration.ofSeconds(10);
            }
        };

        NewRelicRegistry newRelicRegistry = NewRelicRegistry.builder(config)
                .commonAttributes(commonAttributes)
                .build();
        newRelicRegistry.start(new NamedThreadFactory("micrometer-registry"));

```


And, that's it! For details on what your metrics will look like once they are reported, please see the [Micrometer exporter spec](https://github.com/newrelic/exporter-specs). 
