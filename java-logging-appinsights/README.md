# Azure Application Insights

Logging module to auto configure Request Name Interceptor and Web Request Tracking Filter for Azure Application Insights app.

## User guide

The module provides all Telemetry modules and initialisers available from web artifact.

### Basic usage

Maven:

```xml
<dependency>
    <groupId>uk.gov.hmcts.reform</groupId>
    <artifactId>java-logging-appinsights</artifactId>
    <version>1.5.0</version>
</dependency>
```

Gradle:

```groovy
compile group: 'uk.gov.hmcts.reform', name: 'java-logging-appinsights', version: '1.5.0'
```

It will automatically include Request Name interceptor and Request Tracking Filter configurations into spring boot web application.

Request Tracking Filter needs agent to be configured. By default agent uses built in configuration but you can provide your own. There are 2 _problems_ with this:

- Configuration must be named `AI-Agent.xml`
- File must be in the same path as the injected agent jar.

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

Sample code to sync configuration file to lib directory where gradle compiles distribution:

```groovy
distributions {
  main {
    contents {
      from(file("$projectDir/lib/AI-Agent.xml")) {
        into "lib"
      }
    }
  }
}
```

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

Telemetry component stands for TelemetryClient Bean configuration so all the application needs to implement is component as follows:

```java
@Component
public class AppInsights extends AbstractAppInsights {

    public AppInsights(TelemetryClient client) {
        super(client);
    }
}
```
