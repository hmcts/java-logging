package uk.gov.hmcts.reform.logging.provider;

import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

public class AlertLevelJsonProvider extends AbstractRequireJsonProvider {

    public AlertLevelJsonProvider() {
        super("alertLevel");
    }

    @Override
    protected String getValue(AbstractLoggingException exception) {
        return exception.getAlertLevel().name();
    }
}
