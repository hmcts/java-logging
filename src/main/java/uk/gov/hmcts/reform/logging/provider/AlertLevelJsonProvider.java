package uk.gov.hmcts.reform.logging.provider;

import ch.qos.logback.classic.spi.ILoggingEvent;
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
        if (requireAlertLevel) {
            AbstractLoggingException exception = AbstractLoggingException.getFromLogEvent(event);

            if (exception != null) {
                JsonWritingUtils.writeStringField(generator, getFieldName(), exception.getAlertLevel().name());
            }
        }
    }

    public boolean getRequireAlertLevel() {
        return requireAlertLevel;
    }

    public void setRequireAlertLevel(boolean requireAlertLevel) {
        this.requireAlertLevel = requireAlertLevel;
    }
}
