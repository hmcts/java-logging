package uk.gov.hmcts.reform.logging.spring;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.logging.filters.RequestIdsSettingFilter;
import uk.gov.hmcts.reform.logging.filters.RequestStatusLoggingFilter;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class RequestLoggingAutoConfiguration {

    @Bean
    public FilterRegistrationBean requestIdLoggingFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new RequestIdsSettingFilter());
        filterRegistrationBean.setOrder(HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean requestStatusLoggingFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new RequestStatusLoggingFilter());
        filterRegistrationBean.setOrder(HIGHEST_PRECEDENCE + 1);
        return filterRegistrationBean;
    }
}
