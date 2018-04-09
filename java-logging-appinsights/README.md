# Azure Application Insights

Logging module to auto configure Request Name Interceptor and Web Request Tracking Filter for Azure Application Insights app. Module also tracks logback INFO events for `uk.gov.hmcts` loggers

## User guide

The module provides all Telemetry modules and initialisers available from web artifact.

### Basic usage

For now you need to add the HMCTS maven repository: 

Maven:

```xml
<repositories>
    <repository>
        <id>hmcts-maven</id>
        <name>HMCTS Maven</name>
        <url>https://dl.bintray.com/hmcts/hmcts-maven</url>
    </repository>
</repositories>

<dependency>
    <groupId>uk.gov.hmcts.reform</groupId>
    <artifactId>java-logging-appinsights</artifactId>
    <version>2.0.3</version>
</dependency>
```

Gradle:

```groovy
repositories {
  maven {
    url  "https://dl.bintray.com/hmcts/hmcts-maven"
  }
}

dependencies {
  compile group: 'uk.gov.hmcts.reform', name: 'java-logging-appinsights', version: '2.0.3'
}
```

It will automatically include Request Name interceptor and Request Tracking Filter configurations into spring boot web application.

Request Tracking Filter needs agent to be configured. By default agent uses built in configuration but you can provide your own. There are 2 _problems_ with this:

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

For custom telemetry metrics implement `AbstractAppInsights` already provided within module. It contains telemetry client ready for usage.

### Configuration defaults

#### Modules

- WebRequestTrackingTelemetryModule
- WebSessionTrackingTelemetryModule
- WebUserTrackingTelemetryModule

#### Initialisers

- DeviceInfoContextInitializer
- SdkVersionContextInitializer
- SequencePropertyInitializer
- TimestampPropertyInitializer
- WebOperationIdTelemetryInitializer
- WebOperationNameTelemetryInitializer
- WebSessionTelemetryInitializer
- WebUserTelemetryInitializer
- WebUserAgentTelemetryInitializer

#### Developer mode

By default developer mode is off. To turn it on set in configuration manually (for telemetry client to pick it up):

```java
TelemetryConfiguration.getActive().getChannel().setDeveloperMode(true);
```

or simply turn on by configuration:

```yaml
app-insights:
  dev-mode: on
```

#### Flags

```yaml
app-insights:
  dev-mode: on # by default it's not present and turned off
  request-component: on # default
  telemetry-component: on # default
```

Dev mode causes significant overhead in CPU and network bandwidth. But sends each telemetry one by one instantly available on Azure.

Request components stand fo WebRequestNameInterceptor and WebRequestTrackingFilter - automatically configured in library

Request component, WebRequestTrackingFilter in particular, requires application name to be present:

```yaml
spring:
  application:
    name: My Application Insights WebApp
```

In case service does not need request component, it is recommended to exclude auto-injected library from the project dependencies:

```groovy
configurations {
  runtime.exclude group: 'com.microsoft.azure', module: 'applicationinsights-agent'
}
```

Telemetry component stands for TelemetryClient Bean configuration so all the application needs to implement is component as follows:

```java
@Component
public class AppInsights extends AbstractAppInsights {

    public AppInsights(TelemetryClient client) {
        super(client);
    }
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
