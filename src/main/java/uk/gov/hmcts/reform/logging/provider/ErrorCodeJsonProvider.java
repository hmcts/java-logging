package uk.gov.hmcts.reform.logging.provider;

import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

public class ErrorCodeJsonProvider extends AbstractRequireJsonProvider {

    public ErrorCodeJsonProvider() {
        super("errorCode");
    }

    @Override
    protected String getValue(AbstractLoggingException exception) {
        return exception.getErrorCode();
    }
}
