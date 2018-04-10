package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.internal.util.PropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * This is a hack we need until Microsoft delivers proper support for Spring applications. Telemetry initializers
 * defined in ApplicationInsights.xml are not instrumented by Spring, we need to expose the application name via
 * a static method in order to access it.
 */
public final class SpringApplicationName {

    private static final String APPLICATION_NAME_PROPERTIES = "app-name.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringApplicationName.class);

    private static String value;

    static {
        try {
            Properties properties = PropertyHelper.getProperties(APPLICATION_NAME_PROPERTIES);
            value = properties.getProperty("name");
        } catch (IOException exception) {
            LOGGER.error("Unable to read {} for application name", APPLICATION_NAME_PROPERTIES);
        }
    }

    public static String get() {
        return value;
    }
}
