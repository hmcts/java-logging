package uk.gov.hmcts.reform.logging.provider;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

import java.io.IOException;

public abstract class AbstractRequireJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

    private boolean require = true;

    AbstractRequireJsonProvider(String fieldName) {
        super();
        setFieldName(fieldName);
    }

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
        if (require) {
            ThrowableProxy proxy = (ThrowableProxy) event.getThrowableProxy();
            AbstractLoggingException exception = AbstractLoggingException.getFromLogEvent(proxy);

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
