[![Build Status](https://travis-ci.org/hmcts/java-logging.svg?branch=master)](https://travis-ci.org/hmcts/java-logging)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ebac86c131154ef2b59ab302d1d75fd9)](https://www.codacy.com/app/HMCTS/java-logging)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/ebac86c131154ef2b59ab302d1d75fd9)](https://www.codacy.com/app/HMCTS/java-logging)

# Reform Java logging module

A Java module which allows to configure [Logback](https://logback.qos.ch/) to log in a format which we can feed into
Logstash. Uses the [logback-logstash-encoder](https://github.com/logstash/logstash-logback-encoder) to produce JSON
output.

Detailed documentation can be found [here](docs/logging.md)

## Prerequisites

- [Java 8](https://www.oracle.com/java)

## User guide

The module provides a `logback.xml` configuration file which configures Logback to use a format expected by our ELK
stack. It allows a number of configuration options to customize the logging to your needs.

### Basic usage

The module comprises two components; simply add a component as your project's dependency to use it.

#### java-logging-spring

Use for formatting log output in Spring Boot applications.

Maven:
```xml
<dependency>
    <groupId>uk.gov.hmcts.reform</groupId>
    <artifactId>java-logging-spring</artifactId>
    <version>1.2.1</version>
</dependency>
```

Gradle:
```groovy
compile group: 'uk.gov.hmcts.reform', name: 'java-logging-spring', version: '1.2.1'
```

#### java-logging-httpcomponents

Use for adding request IDs to external HTTP / HTTPS requests.

Maven:
```xml
<dependency>
    <groupId>uk.gov.hmcts.reform</groupId>
    <artifactId>java-logging-httpcomponents</artifactId>
    <version>1.2.1</version>
</dependency>
```

Gradle:
```groovy
compile group: 'uk.gov.hmcts.reform', name: 'java-logging-httpcomponents', version: '1.2.1'
```

**Please note:** You will also need to implement a class that configures an HTTP client with interceptors for outbound HTTP requests and responses. See https://github.com/hmcts/cmc-claim-store/blob/master/src/main/java/uk/gov/hmcts/cmc/claimstore/clients/RestClient.java#L98 for an example.

After that you can log like you would do with any [SLF4J](https://www.slf4j.org/) logger. Define it as a class field:

```java
private static final Logger log = LoggerFactory.getLogger(SomeResource.class);
```

And do the actual logging, e.g.:

```java
log.info("An important business process has finished");
```

### Configuration defaults

By default the module will use a simple, human-friendly logging format which can be used out-of-the-box for development:

```
2017-02-02 12:22:23,647 INFO [main] io.dropwizard.assets.AssetsBundle: Registering AssetBundle with name: swagger-assets for path /swagger-static/*
2017-02-02 12:22:23,806 INFO [main] org.reflections.Reflections: Reflections took 96 ms to scan 1 urls, producing 79 keys and 87 values
2017-02-02 12:22:24,835 INFO [main] io.dropwizard.server.DefaultServerFactory: Registering jersey handler with root path prefix: /
```

Root logging level will be set to `INFO`. It can be adjusted by setting a `ROOT_LOGGING_LEVEL` environment variable.

### Changing the output format

Changing the output format to JSON can be done by setting an environment variable:

```bash
ROOT_APPENDER="JSON_CONSOLE"
```

The result will be similar to something like this:

```
{"timestamp":"2017-02-02T12:29:43.749+00:00","level":"INFO","message":"Registering AssetBundle with name: swagger-assets for path /swagger-static/*","type":"java","microservice":"claim-store","team":"cmc","hostname":"ultron.local","environment":"undefined"}
{"timestamp":"2017-02-02T12:29:43.860+00:00","level":"INFO","message":"Reflections took 62 ms to scan 1 urls, producing 79 keys and 87 values ","type":"java","microservice":"claim-store","team":"cmc","hostname":"ultron.local","environment":"undefined"}
{"timestamp":"2017-02-02T12:29:44.673+00:00","level":"INFO","message":"Registering jersey handler with root path prefix: /","type":"java","microservice":"claim-store","team":"cmc","hostname":"ultron.local","environment":"undefined"}
```

JSON output can be made more human-friendly with a pretty printing environment variable:

```bash
JSON_CONSOLE_PRETTY_PRINT="true"
```

Which produces:

```
{
  "timestamp" : "2017-02-02T12:32:38.123+00:00",
  "level" : "INFO",
  "message" : "Registering AssetBundle with name: swagger-assets for path /swagger-static/*",
  "type" : "java",
  "microservice" : "undefined",
  "team" : "undefined",
  "hostname" : "ultron.local",
  "environment" : "undefined"
}
{
  "timestamp" : "2017-02-02T12:32:38.252+00:00",
  "level" : "INFO",
  "message" : "Reflections took 80 ms to scan 1 urls, producing 79 keys and 87 values ",
  "type" : "java",
  "microservice" : "undefined",
  "team" : "undefined",
  "hostname" : "ultron.local",
  "environment" : "undefined"
}
{
  "timestamp" : "2017-02-02T12:32:39.142+00:00",
  "level" : "INFO",
  "message" : "Registering jersey handler with root path prefix: /",
  "type" : "java",
  "microservice" : "undefined",
  "team" : "undefined",
  "hostname" : "ultron.local",
  "environment" : "undefined"
}
```

If you want to log any extra fields you can use the [StructuredArguments](https://github.com/logstash/logstash-logback-encoder#event-specific-custom-fields)
feature like this:

```java
log.info("An important business process has finished", keyValue("transactionId", id));
```

This will result in the following output:

```
{
  "timestamp" : "2017-02-03T11:15:39.077+00:00",
  "level" : "INFO",
  "message" : "An important business process has finished",
  "type" : "java",
  "microservice" : "undefined",
  "team" : "undefined",
  "hostname" : "ultron.local",
  "environment" : "undefined",
  "fields" : {
    "transactionId" : 1234567
  }
}
```

### Service details configuration

As can be seen above the JSON format logs additional metadata information which is configurable via environment variables:
- `REFORM_SERVICE_TYPE` which defaults to *java*,
- `REFORM_SERVICE_NAME` which defaults to *undefined*,
- `REFORM_TEAM` which defaults to *undefined*,
- `REFORM_ENVIRONMENT` which defaults to *undefined*,
- `HOSTNAME` which defaults to *undefined*, but in general should by set by the OS.

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

## Future development considerations

- [MDC](https://logback.qos.ch/manual/mdc.html) for capturing and logging request identifiers.
