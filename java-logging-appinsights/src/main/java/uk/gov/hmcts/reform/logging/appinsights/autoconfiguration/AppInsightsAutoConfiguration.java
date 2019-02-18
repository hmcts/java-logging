package uk.gov.hmcts.reform.logging.appinsights.autoconfiguration;

import com.microsoft.applicationinsights.autoconfigure.initializer.SpringBootTelemetryInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers.ContextVersionInitializer;

/**
 * <h1>Core Application Insights Configuration</h1> .
 *
 * <p>This class provides the custom Configuration for AppInsights .</p>
 */
public class AppInsightsAutoConfiguration {

    /**
     * Bean for ContextVersionInitializer.
     *
     * @return instance of {@link ContextVersionInitializer}
     */
    @Bean
    @ConditionalOnBean(SpringBootTelemetryInitializer.class)
    public ContextVersionInitializer contextVersionInitializer() {
        return new ContextVersionInitializer();
    }

}
