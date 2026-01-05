[![GitHub version](https://badge.fury.io/gh/hmcts%2Fjava-logging.svg)](https://badge.fury.io/gh/hmcts%2Fjava-logging)
[![Known Vulnerabilities](https://snyk.io/test/github/hmcts/java-logging/badge.svg)](https://snyk.io/test/github/hmcts/java-logging)

# Java logging

A Java module which standardises the logging for the reform projects.

## Prerequisites

- [Java 17](https://adoptium.net/)

## User guide

The module provides a `logback.xml` configuration file which configures Logback to use a default reform format.
It allows a number of configuration options to customize the logging to your needs.


### Basic usage

Simply add base component as your project's dependency and then one or more of three components discussed below to use it.

Base component dependency, gradle:
```groovy
repositories {
    maven {
        url 'https://pkgs.dev.azure.com/hmcts/Artifacts/_packaging/hmcts-lib/maven/v1'
    }
}

implementation group: 'com.github.hmcts.java-logging', name: 'logging', version: 'LATEST_TAG'
```

#### Application Insights

The `java-logging-appinsights` module has been removed in version 7.0.0. If upgrading from 6.x, remove this dependency:
```gradle
// Remove this line:
implementation group: 'com.github.hmcts.java-logging', name: 'logging-appinsights', version: 'x.x.x'
```

##### Application Insights Agent Setup

Teams now add the Application Insights agent directly to their Dockerfile, for example it might look like this:

```dockerfile
# renovate: datasource=github-releases depName=microsoft/ApplicationInsights-Java
ARG APP_INSIGHTS_AGENT_VERSION=3.4.9
...

COPY lib/applicationinsights.json /opt/app/
...

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

### Releasing

Before releasing a new version create a PR and run the following script to update the README to use the new version number.

```bash
./prepare-for-release.sh
```

To publish a new release create a new release via the GitHub UI. The tag selected will be used as the version number.
