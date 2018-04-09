package uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This is a hack we need until Microsoft delivers proper support for Spring applications. Telemetry initializers
 * defined in ApplicationInsights.xml are not instrumented by Spring, we need to expose the application name via
 * a static method in order to access it.
 */
@Component
public class CloudRoleName {
    private static String springApplicationName;

    public static String get() {
        return springApplicationName;
    }

    @Value("${spring.application.name}")
    public void setCloudRoleName(String springApplicationName) {
        CloudRoleName.springApplicationName = springApplicationName;
    }
}
