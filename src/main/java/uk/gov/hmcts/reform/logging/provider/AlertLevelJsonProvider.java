package uk.gov.hmcts.reform.logging.provider;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

import java.io.IOException;

public class AlertLevelJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

    private static final String FIELD_ALERT_LEVEL = "alertLevel";

    private boolean requireAlertLevel = true;

    public AlertLevelJsonProvider() {
        setFieldName(FIELD_ALERT_LEVEL);
    }

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
        AbstractLoggingException exception = null;

        if (requireAlertLevel && event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            Throwable eventException = ((ThrowableProxy) event.getThrowableProxy()).getThrowable();

            if (eventException instanceof AbstractLoggingException) {
                exception = (AbstractLoggingException) eventException;
            } else {
                //triggerBadImplementationLog(eventException);
            }
        }

        if (exception != null) {
            JsonWritingUtils.writeStringField(generator, getFieldName(), exception.getAlertLevel().name());
        }
    }

    public boolean getRequireAlertLevel() {
        return requireAlertLevel;
    }

    public void setRequireAlertLevel(boolean requireAlertLevel) {
        this.requireAlertLevel = requireAlertLevel;
    }
}
