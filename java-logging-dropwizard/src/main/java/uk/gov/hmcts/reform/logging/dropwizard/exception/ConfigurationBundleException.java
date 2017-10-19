package uk.gov.hmcts.reform.logging.dropwizard.exception;

public class ConfigurationBundleException extends RuntimeException {

    public ConfigurationBundleException(Throwable exception) {
        super(exception);
    }
}
