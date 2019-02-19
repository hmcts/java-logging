package uk.gov.hmcts.reform.logging.appinsights.autoconfiguration;

import com.microsoft.applicationinsights.autoconfigure.initializer.SpringBootTelemetryInitializer;
import com.microsoft.applicationinsights.extensibility.initializer.SequencePropertyInitializer;
import com.microsoft.applicationinsights.extensibility.initializer.TimestampPropertyInitializer;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebSyntheticRequestTelemetryInitializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers.ContextInitializer;

/**
 * <h1>Core Application Insights Configuration</h1> .
 *
 * <p>This class provides the custom Configuration for AppInsights .</p>
 */
public class AppInsightsAutoConfiguration {

    /**
     * Bean for ContextInitializer.
     *
     * @return instance of {@link ContextInitializer}
     */
    @Bean
    @ConditionalOnBean(SpringBootTelemetryInitializer.class)
    public ContextInitializer contextInitializer() {
        return new ContextInitializer();
    }

    @Bean
    public TimestampPropertyInitializer timestampPropertyInitializer() {
        return new TimestampPropertyInitializer();
    }

    @Bean
    public SequencePropertyInitializer sequencePropertyInitializer() {
        return new SequencePropertyInitializer();
    }

    @Bean
    public WebSyntheticRequestTelemetryInitializer webSyntheticRequestTelemetryInitializer() {
        return new WebSyntheticRequestTelemetryInitializer();
    }

}
