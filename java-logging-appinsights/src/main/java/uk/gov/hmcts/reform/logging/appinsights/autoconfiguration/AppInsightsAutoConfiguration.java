package uk.gov.hmcts.reform.logging.appinsights.autoconfiguration;

import com.microsoft.applicationinsights.extensibility.initializer.SequencePropertyInitializer;
import com.microsoft.applicationinsights.extensibility.initializer.TimestampPropertyInitializer;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebSyntheticRequestTelemetryInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers.ContextVersionInitializer;

/**
 * Core Application Insights Configuration.
 *
 * <p>
 * This class provides the custom Configuration for AppInsights.
 */
public class AppInsightsAutoConfiguration {

    public static final String HAVING_VALUE_TRUE = "true";

    /**
     * Bean for ContextVersionInitializer.
     *
     * @return instance of {@link ContextVersionInitializer}
     */
    @Bean
    @ConditionalOnProperty(value = "application-insights.custom.modules.ContextVersionInitializer.enabled",
        havingValue = HAVING_VALUE_TRUE, matchIfMissing = true)
    public ContextVersionInitializer contextVersionInitializer() {
        return new ContextVersionInitializer();
    }

    @Bean
    @ConditionalOnProperty(value = "application-insights.default.modules.TimestampPropertyInitializer.enabled",
        havingValue = HAVING_VALUE_TRUE, matchIfMissing = true)
    public TimestampPropertyInitializer timestampPropertyInitializer() {
        return new TimestampPropertyInitializer();
    }

    @Bean
    @ConditionalOnProperty(value = "application-insights.default.modules.SequencePropertyInitializer.enabled",
        havingValue = HAVING_VALUE_TRUE, matchIfMissing = true)
    public SequencePropertyInitializer sequencePropertyInitializer() {
        return new SequencePropertyInitializer();
    }

    @Bean
    @ConditionalOnProperty
        (value = "application-insights.default.modules.WebSyntheticRequestTelemetryInitializer.enabled",
        havingValue = HAVING_VALUE_TRUE, matchIfMissing = true)
    public WebSyntheticRequestTelemetryInitializer webSyntheticRequestTelemetryInitializer() {
        return new WebSyntheticRequestTelemetryInitializer();
    }

}
