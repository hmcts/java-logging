package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@SpringBootApplication
@SuppressWarnings("HideUtilityClassConstructor")
public class SpringBootTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootTestApplication.class, args);
    }

    @Component
    @ConditionalOnProperty(name = "app-insights.telemetry-component", havingValue = "true", matchIfMissing = true)
    public static class AppInsightsImp extends AbstractAppInsights {
        public AppInsightsImp(TelemetryClient telemetry) {
            super(telemetry);
        }
    }
}
