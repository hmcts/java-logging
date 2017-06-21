package uk.gov.hmcts.reform.logging;

import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.decorate.JsonGeneratorDecorator;

import static java.lang.System.getenv;

/**
 * Enables pretty printing for the JSON logging output.
 *
 * <p>Disabled by default, can be enabled via a environment variable.</p>
 */
public class PrettyPrintingDecorator implements JsonGeneratorDecorator {

    public static final String JSON_CONSOLE_PRETTY_PRINT = "JSON_CONSOLE_PRETTY_PRINT";

    @Override
    public JsonGenerator decorate(JsonGenerator generator) {
        if (prettyPrintingEnabled()) {
            generator.useDefaultPrettyPrinter();
        }
        return generator;
    }

    private boolean prettyPrintingEnabled() {
        return Boolean.valueOf(getenv(JSON_CONSOLE_PRETTY_PRINT));
    }

}
