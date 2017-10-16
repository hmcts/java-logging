package uk.gov.hmcts.reform.logging.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class SpringBootTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootTestApplication.class, args);
    }

    @RestController
    public static class TestController {

        private class RequestMappingException extends RuntimeException {
            public RequestMappingException(String message) {
                super(message);
            }
        }

        @RequestMapping("/public")
        public String publicEndpoint() {
            return "OK";
        }

        @RequestMapping("/protected")
        public String protectedEndpoint() {
            return "OK";
        }

        @RequestMapping("/failing")
        public String failingEndpoint() throws RequestMappingException {
            throw new RequestMappingException("Failing endpoint");
        }
    }

    @Configuration
    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/public").permitAll()
                    .antMatchers("/failing").permitAll()
                    .antMatchers("/protected").authenticated();
        }
    }
}
