# Azure Application Insights

Logging module to auto configure necessary components for Azure Application Insights app.
It uses [azure-application-insights-spring-boot-starter](https://github.com/Microsoft/ApplicationInsights-Java/tree/v2.3.1/azure-application-insights-spring-boot-starter) and adds some specific initializers as Spring beans.
Module also tracks logback INFO events for `uk.gov.hmcts` loggers

## User guide

The module provides all Telemetry modules and initializers available from web artifact.

### Basic usage

Gradle:

```groovy
repositories {
  maven {
    url 'https://pkgs.dev.azure.com/hmcts/Artifacts/_packaging/hmcts-lib/maven/v1'
  }
}

dependencies {
  implementation group: 'com.github.hmcts.java-logging', name: 'logging-appinsights', version: 'LATEST_TAG'
}
```

Note: if you're on an older version of Spring Boot you may need to bring it and the logging-spring libraries in on deprecated versions:
```groovy
dependencies {
  implementation group: 'com.github.hmcts', name: 'java-logging', version: '6.1.8'
  implementation group: 'com.github.hmcts.java-logging', name: 'logging-spring', version: '5.1.9'
  implementation group: 'com.github.hmcts.java-logging', name: 'logging-appinsights', version: '5.1.9'
}
```

It will automatically include Request Name interceptor and Request Tracking Filter configurations into spring boot web application.

Request Tracking Filter needs agent to be configured. By default agent uses built in configuration but you can provide your own.

Sample of `AI-Agent.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<ApplicationInsightsAgent>
    <AgentLogger>INFO</AgentLogger>
    <Instrumentation>
        <BuiltIn enabled="true">
            <HTTP enabled="true"/>
        </BuiltIn>
        <Class name="uk.gov.hmcts.reform.demo.services.DemoService">
            <Method name="doSomething" reportCaughtExceptions="true" reportExecutionTime="true"/>
        </Class>
    </Instrumentation>
</ApplicationInsightsAgent>
```

This file must be placed in a `<repository-root>/lib` directory for applications running on CNP along with the agent jar that matches the current app insights version here
Retrieve the jar from github, i.e. https://github.com/Microsoft/ApplicationInsights-Java/releases

TelemetryClient can be `autowired`  to implement custom telemetry metrics.

#### Provide App Insights Connection Key
Microsoft ended support for Instrumentation Key Ingestion on March 31, 2025.

An app-insight-connection-key is provided in the Key Vault Secrets. (Note: It's sometimes spelled app-insights-connection-key.)

Add to your projects secrets in cnp-flux-config:
```
- name: app-insight-connection-key
  alias: app-insight-connection-key
```

On your project:
1. Add a applicationinsights.json file with connection key path in the lib/ directory
```
{
  "connectionString": "${file:/mnt/secrets/adoption/app-insight-connection-key}",
  "role": {
    "name": "HMCTS MyProject"
  }
}
```
More information: https://docs.azure.cn/en-us/azure-monitor/app/java-standalone-config

2. Add a step in your Dockerfile to move the new applicationinsights.json file to the correct location:
```
COPY lib/applicationinsights.json /opt/app/
```


### Optional Configuration defaults

#### Modules configured by spring starter

- WebRequestTrackingTelemetryModule
- WebSessionTrackingTelemetryModule
- WebUserTrackingTelemetryModule

#### Initializers configured by spring starter


##### Context

- DeviceInfoContextInitializer
- SdkVersionContextInitializer

##### Telemetry

- WebOperationIdTelemetryInitializer
- WebOperationNameTelemetryInitializer
- WebSessionTelemetryInitializer
- WebSyntheticRequestTelemetryInitializer
- WebUserTelemetryInitializer
- WebUserAgentTelemetryInitializer
- CloudInfoContextInitializer
- SpringBootTelemetryInitializer

#### Initializers configured by logging-appinsights

##### Context
ContextInitializer ( Custom Initializer to set component version to context)

##### Telemetry

- TimestampPropertyInitializer
- SequencePropertyInitializer
- WebSyntheticRequestTelemetryInitializer


`WebSyntheticRequestTelemetryInitializer` provides functionality to separate out requests via so called Synthetic Source tag.
All headers are available in helper class [SyntheticHeaders](java-logging-appinsights/src/main/java/uk/gov/hmcts/reform/logging/appinsights/SyntheticHeaders).
All headers are optional and by default will be assumed `Application Insights Availability Monitoring` as Synthetic source name.
Additionally default setup tries to include `SyntheticTest-RunId` and `SyntheticTest-Location` if present.
For particular requests required to be separated, case can provide additional header `SyntheticTest-Source` which will replace default source name.
Along with custom Source name there are more optional headers to be applied at will:

- `SyntheticTest-UserId`
- `SyntheticTest-SessionId`
- `SyntheticTest-OperationId`
- `SyntheticTest-TestName`
- `SyntheticTest-RunId`
- `SyntheticTest-Location`

[SyntheticHeaders](java-logging-appinsights/src/main/java/uk/gov/hmcts/reform/logging/appinsights/SyntheticHeaders) might be deprecated in future based on Microsoft making the constants public.

#### To disable Application insights completely

Refer [Spring Starter disabling App Insights](https://github.com/Microsoft/ApplicationInsights-Java/tree/v2.3.1/azure-application-insights-spring-boot-starter#completely-disable-application-insights-using-applicationproperties)

#### Developer mode

By default developer mode is disabled. This is controlled by starter and to turn it on , set the configuration manually (for telemetry client to pick it up):

```java
TelemetryConfiguration.getActive().getChannel().setDeveloperMode(true);
```

or simply turn on by configuration:

```yaml
azure:
  application-insights:
    channel:
      in-process:
        developer-mode: true

```

Dev mode causes significant overhead in CPU and network bandwidth. But sends each telemetry one by one instantly available on Azure.

Request components stand fo WebRequestNameInterceptor and WebRequestTrackingFilter - automatically configured in library

Request component, WebRequestTrackingFilter in particular, requires application name to be present:

```yaml
spring:
  application:
    name: My Application Insights WebApp
```

This configuration entry is also used by [`SpringBootTelemetryInitializer`](https://github.com/Microsoft/ApplicationInsights-Java/blob/v2.3.1/azure-application-insights-spring-boot-starter/src/main/java/com/microsoft/applicationinsights/autoconfigure/initializer/SpringBootTelemetryInitializer.java) to set the `cloud_Role` App Insights tag. This makes distinguishing between services easier in cases where a number of related services uses the same App Insights instance.


#### Flags

For Spring starter configurations , Refer [configure-more-parameters-using-applicationproperties](https://github.com/Microsoft/ApplicationInsights-Java/tree/v2.3.1/azure-application-insights-spring-boot-starter#configure-more-parameters-using-applicationproperties)

Additional properties :
```properties
application-insights.custom.modules.ContextInitializer.enabled=true
application-insights.default.modules.TimestampPropertyInitializer.enabled=true
application-insights.default.modules.SequencePropertyInitializer.enabled=true
application-insights.default.modules.WebSyntheticRequestTelemetryInitializer.enabled=true

```


In case service does not need request component, it is recommended to exclude auto-injected library from the project dependencies:

```groovy
configurations {
  runtime.exclude group: 'com.microsoft.azure', module: 'applicationinsights-agent'
}
```

#### Product version

AppInsights uses application version in most if not all metrics. This is now set via MANIFEST property:

```manifest
Implementation-Version: 0.0.1
```

In spring boot template this will be set by default in jar task where name of the file is defined for CNP deployment. Example:

```groovy
jar {
  archiveName 'spring-boot-template.jar'

  manifest {
    attributes('Implementation-Version': project.version.toString())
  }
}
```
