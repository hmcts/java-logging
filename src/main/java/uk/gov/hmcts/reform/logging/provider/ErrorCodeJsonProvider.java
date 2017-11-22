package uk.gov.hmcts.reform.logging.provider;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

import java.io.IOException;

public class ErrorCodeJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

    private static final String FIELD_ERROR_CODE = "errorCode";

    private boolean requireErrorCode = true;

    public ErrorCodeJsonProvider() {
        setFieldName(FIELD_ERROR_CODE);
    }

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
        if (requireErrorCode) {
            AbstractLoggingException exception = AbstractLoggingException.getFromLogEvent(event);

            if (exception != null) {
                JsonWritingUtils.writeStringField(generator, getFieldName(), exception.getErrorCode());
            }
        }
    }

    public boolean isRequireErrorCode() {
        return requireErrorCode;
    }

    public void setRequireErrorCode(boolean requireErrorCode) {
        this.requireErrorCode = requireErrorCode;
    }
}
