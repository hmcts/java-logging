package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@SpringBootApplication
@SuppressWarnings("HideUtilityClassConstructor")
public class SpringBootTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootTestApplication.class, args);
    }

    @Configuration
    public class AppInsightsConfiguration {

        @Bean
        public TelemetryClient getTelemetryClient() {
            return new TelemetryClient();
        }
    }

    @Component
    public static class AppInsightsImp extends AbstractAppInsights {
        public AppInsightsImp(TelemetryClient telemetry, @Value("${app-insights.dev-mode}") boolean devMode) {
            super(telemetry, devMode);
        }
    }
}
