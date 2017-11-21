package uk.gov.hmcts.reform.logging.provider;

import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders;

public class EventJsonProviders extends LoggingEventJsonProviders {

    public void addAlertLevel(AlertLevelJsonProvider provider) {
        addProvider(provider);
    }

    public void addErrorCode(ErrorCodeJsonProvider provider) {
        addProvider(provider);
    }
}
