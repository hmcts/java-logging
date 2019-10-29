[![Build Status](https://travis-ci.org/hmcts/java-logging.svg?branch=master)](https://travis-ci.org/hmcts/java-logging)
[![GitHub version](https://badge.fury.io/gh/hmcts%2Fjava-logging.svg)](https://badge.fury.io/gh/hmcts%2Fjava-logging)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ebac86c131154ef2b59ab302d1d75fd9)](https://www.codacy.com/app/HMCTS/java-logging)
[![codecov](https://codecov.io/gh/hmcts/java-logging/branch/master/graph/badge.svg)](https://codecov.io/gh/hmcts/java-logging)
[![Known Vulnerabilities](https://snyk.io/test/github/hmcts/java-logging/badge.svg)](https://snyk.io/test/github/hmcts/java-logging)
[ ![Download](https://api.bintray.com/packages/hmcts/hmcts-maven/logging/images/download.svg) ](https://bintray.com/hmcts/hmcts-maven/logging/_latestVersion)

# Reform Java logging module

A Java module which standardises the logging for the reform projects.

## Prerequisites

- [Java 8](https://www.oracle.com/java)

## User guide

The module provides a `logback.xml` configuration file which configures Logback to use a default reform format.
It allows a number of configuration options to customize the logging to your needs.


### Basic usage

The module comprises of three components; simply add a component as your project's dependency to use it.

#### java-logging-insights

Use for automatic configuration of Azure Application Insights for a Spring Boot project. [Read more](java-logging-appinsights/README.md)

#### java-logging-spring

Use for formatting log output in Spring Boot applications.


Gradle:
```groovy
compile group: 'uk.gov.hmcts.reform', name: 'logging-spring', version: '5.1.1-BETA'
```

### Configuration defaults

By default the module will use a simple, human-friendly logging format which can be used out-of-the-box for development:

```
2017-02-02 12:22:23,647 INFO [main] io.dropwizard.assets.AssetsBundle: Registering AssetBundle with name: swagger-assets for path /swagger-static/*
2017-02-02 12:22:23,806 INFO [main] org.reflections.Reflections: Reflections took 96 ms to scan 1 urls, producing 79 keys and 87 values
2017-02-02 12:22:24,835 INFO [main] io.dropwizard.server.DefaultServerFactory: Registering jersey handler with root path prefix: /
```

Root logging level will be set to `INFO`. It can be adjusted by setting a `ROOT_LOGGING_LEVEL` environment variable.

### Additional Logback configuration:

Additional Logback configuration can be provided by adding a `logback-includes.xml` file to the classpath root
(just drop it in the `main/resources` folder).
This allows to define any configuration allowed by Logback XML config, where a typical usage could be defining more
specific loggers, e.g.:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<included>
  <logger name="uk.goc.hmcts.reform" level="DEBUG"/>
  <logger name="uk.goc.hmcts.reform.resources" level="WARN"/>
</included>
```

Path to this file can adjusted by setting a `LOGBACK_INCLUDES_FILE` environment variable.

Logback can print additional information while processing its configuration files. This can be enabled by setting
`LOGBACK_CONFIGURATION_DEBUG` variable to `true`.

Log pattern related configurations:

| variable                    | default                     |
| --------------------------- | --------------------------- |
| LOGBACK_DATE_FORMAT         | yyyy-MM-dd'T'HH:mm:ss.SSSZZ |
| EXCEPTION_LENGTH            | 50                          |
| LOGGER_LENGTH               | 50                          |
| CONSOLE_LOG_PATTERN         | %d{${LOGBACK_DATE_FORMAT}} %-5level [%thread] %logger{${LOGGER_LENGTH}}%ex{${EXCEPTION_LENGTH}} %msg%n}                        |

where
 - LOGBACK_DATE_FORMAT: Date format is default logstash encoder date format. `REQUIRE` fields are flags representing show/hide feature.
 - EXCEPTION_LENGTH: how many lines to show in an exception stack trace ( per exception not including causes)
 - LOGGER_LENGTH: how long the logger name can be before logback starts abbreviating the package names

## Development guide

[Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) will automatically download a
project-local [Gradle](https://gradle.org/) distribution the first time you run any of the `gradlew` commands below.

### Tests and verification

To run all unit tests:

```bash
./gradlew test
```

To execute [Checkstyle](http://checkstyle.sourceforge.net/) and [PMD](http://pmd.sourceforge.net/) checks:

```bash
./gradlew check
```

You can also execute both via:

```bash
./gradlew build
```

### Installing

To install the artifact to a local Maven repository:
```bash
./gradlew install
```

### Exception logging

Since [v1.5.0](https://github.com/hmcts/java-logging/releases/tag/1.5.0) Alert level and error code are required fields for any exception to be logged.
In order to correctly stream log events for all exceptions one must be extended with `AbstractLoggingException`.
Error code is introduced as legacy error group not minding the fact exceptions themselves represent relevant error group.
There is a helper `UnknownErrorCodeException` class which populates the field with `UNKNOWN` as error code.

Alert level is still required.

### Releasing

Run the script `./prepare-for-release.sh` select an appropriate version and follow the instructions the script provides

## Future development considerations

- [MDC](https://logback.qos.ch/manual/mdc.html) for capturing and logging request identifiers.
