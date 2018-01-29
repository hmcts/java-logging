package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;
import com.microsoft.applicationinsights.web.spring.internal.InterceptorRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@Import(InterceptorRegistry.class)
public class AppInsightsAutoConfiguration {

    @Bean
    public FilterRegistrationBean registerWebRequestTrackingFilter(
        @Value("${spring.application.name}") String appName
    ) {
        FilterRegistrationBean bean = new FilterRegistrationBean();

        bean.setFilter(new WebRequestTrackingFilter(appName));

        return bean;
    }
}
