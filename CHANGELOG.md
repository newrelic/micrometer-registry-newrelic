# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
