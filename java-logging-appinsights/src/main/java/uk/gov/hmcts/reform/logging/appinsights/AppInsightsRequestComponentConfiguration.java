package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;
import com.microsoft.applicationinsights.web.spring.internal.InterceptorRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(
    prefix = "app-insights",
    name = "request-component",
    havingValue = "true",
    matchIfMissing = true
)
@ConditionalOnWebApplication
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@Import(InterceptorRegistry.class)
public class AppInsightsRequestComponentConfiguration {

    @Bean("webRequestTrackingFilter")
    public FilterRegistrationBean registerWebRequestTrackingFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();

        bean.setFilter(new WebRequestTrackingFilter(SpringApplicationName.get()));

        return bean;
    }
}
