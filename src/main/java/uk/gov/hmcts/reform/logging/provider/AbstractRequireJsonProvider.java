package uk.gov.hmcts.reform.logging.provider;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

import java.io.IOException;

public abstract class AbstractRequireJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

    private boolean require = true;

    AbstractRequireJsonProvider(String fieldName) {
        setFieldName(fieldName);
    }

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
        if (require) {
            AbstractLoggingException exception = AbstractLoggingException.getFromLogEvent(event);

            if (exception != null) {
                JsonWritingUtils.writeStringField(generator, getFieldName(), getValue(exception));
            }
        }
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }

    protected abstract String getValue(AbstractLoggingException exception);
}
