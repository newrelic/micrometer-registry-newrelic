# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.10.0] - TBD

## [0.9.0] - 2022-05-27
- Update to telemetry sdk 0.13.2 to address [CVE-2022-25647](https://github.com/advisories/GHSA-4jrv-ppp4-jm57)

## [0.8.0] - 2021-06-09
- add `useLicenseKey` option to NewRelicRegistryConfig. The registry will now accept a New Relic license key to report data. Thank you [Greg Eales](https://github.com/0x006EA1E5)!
- Update io.micrometer:micrometer-core to 1.7.0
- Update com.newrelic.telemetry:telemetry to com.newrelic.telemetry:telemetry-core 0.12.0

## [0.7.0] - 2021-02-18
- Update io.micrometer:micrometer-core to 1.6.4
- Update com.newrelic.telemetry:telemetry to com.newrelic.telemetry:telemetry-core 0.11.0

## [0.6.0] - 2020-10-27
- **BREAKING CHANGE**: [Repackage](https://github.com/newrelic/micrometer-registry-newrelic/pull/104) registry class to `com.newrelic.telemetry.micrometer.NewRelicRegistry`.
  Users will need to change code or configuration that references the old package.
- Shut down the telemetry sdk in [`close()`](https://github.com/newrelic/micrometer-registry-newrelic/pull/82/)
  in order to flush the last batch of telemetry. 
- Depend directly on slf4j
- Change [construction of NewRelicRegistry](https://github.com/newrelic/micrometer-registry-newrelic/pull/85) to avoid deprecations in the SDK.
- Endpoint URI overrides will now respect path information (it was previously ignored).
- Upgrade to micrometer-core 1.5.4
- Update to New Relic Telemetry SDK 0.9.0

## [0.5.0] - 2020-04-01
- Changed: Update micrometer-core version to 1.3.3
- Changed: Update to com.newrelic.telemetry:telemetry:0.4.0

## [0.4.0] - 2020-01-09
- Append library-identifying string to HTTP `User-Agent` header as suffix
- Add ability to enable "audit logging" for more verbose troubleshooting
- `DistributionSummary` now uses a `Summary` instead of a `Count` and 2 `Gauge`s.
- `FunctionTimer` now uses a `Summary` instead of a `Count` and 2 `Gauge`s. 
- `Timer` now uses a `Summary` instead of a `Count` and 3 `Gauge`s.  

## [0.3.0] - 2019-12-18
- Changed: Updated the jackson-databind transitive dependency and mockserver-netty dependency to address a security vulnerability.
- Changed: udpate to NewRelic telemetry SDK 0.3.3 

## [0.2.3] - 2019-10-30
- Changed: Updated the jackson-databind transitive dependency to address a security vulnerability.

## [0.2.2] - 2019-09-13
- Changed: The `source.type` attribute is no longer added to generated metrics.

## [0.2.1] - 2019-09-10
- Changed: Don't require the metric API URI to be provided, but use the default by default.

## [0.2.0] - 2019-09-10
- Changed: update to NewRelic telemetry SDK 0.3.1
- Changed: at registry startup, log the version of the exporter
- Changed: add `collector.name` and `collector.version` attributes to identify micrometer applications
- Changed: normalize attribute names for consistency

## [0.1.0] - 2019-09-01
- Initial public release of the New Relic dimensional-metrics-based Micrometer Registry
