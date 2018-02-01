package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class AppInsightsTelemetryComponentConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AppInsightsTelemetryComponentConfiguration.class);

    @Value("${app-insights.dev-mode:off}")
    private boolean devMode;

    @PostConstruct
    public void setDevelopmentMode() {
        if (devMode) {
            log.warn("Setting developer mode ON for telemetry channel");

            TelemetryConfiguration.getActive().getChannel().setDeveloperMode(true);
        }
    }

    @Bean("telemetryClient")
    @ConditionalOnProperty(
        prefix = "app-insights",
        name = "telemetry-component",
        havingValue = "true",
        matchIfMissing = true
    )
    public TelemetryClient getTelemetryClient() {
        return new TelemetryClient();
    }
}
