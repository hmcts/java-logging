package uk.gov.hmcts.reform.logging.dropwizard.bundles;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.LoggerFactory;

/**
 * A Dropwizard bundle which ensures that Logback is configured via provided XML resource configuration.
 *
 * <p>Although Logback will correctly load its {@code logback.xml} configuration on application startup, DW will then
 * discard that config and use its own one. This is because of DW's design philosophy where they enforce all the
 * configuration to be in a YAML file.</p>
 *
 * <p>To use just add it like any other DW bundle:
 * <pre>
 *     bootstrap.addBundle(new LogbackConfigurationBundle());
 * </pre>
 * If you would like to provide your own {@code logback.xml} file, pass the name of the classpath resource as a
 * constructor argument.</p>
 */
public class LogbackConfigurationBundle implements Bundle {

    private static final String DEFAULT_LOGBACK_CONFIG_FILE = "logback.xml";

    private String configurationFile;

    public LogbackConfigurationBundle() {
        this(DEFAULT_LOGBACK_CONFIG_FILE);
    }

    public LogbackConfigurationBundle(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        // Nothing to do
    }

    @Override
    public void run(Environment environment) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(ClassLoader.getSystemResourceAsStream(configurationFile));
        } catch (JoranException e) {
            throw new RuntimeException(e);
        }
    }

}
