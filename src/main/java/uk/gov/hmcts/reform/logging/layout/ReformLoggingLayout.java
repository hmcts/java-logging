package uk.gov.hmcts.reform.logging.layout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ReformLoggingLayout extends LayoutBase<ILoggingEvent> {

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

    /**
     * By default require thread to be logged.
     */
    private boolean requireThread = true;

    /**
     * By default require an AlertLevel tag to be present for any exception log.
     */
    private boolean requireAlertLevel = true;

    public void setDateFormat(String format) {
        dateFormat = DateTimeFormatter.ofPattern(format);
    }

    public void setRequireThread(boolean requireThread) {
        this.requireThread = requireThread;
    }

    public void setRequireAlertLevel(boolean requireAlertLevel) {
        this.requireAlertLevel = requireAlertLevel;
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        Instant instant = Instant.ofEpochMilli(event.getTimeStamp());
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        StringBuilder log = new StringBuilder(dateFormat.format(dateTime));

        log.append(" ");
        log.append(String.format("%-5s", event.getLevel().levelStr));

        if (requireThread) {
            log.append(String.format(" [%s]", event.getThreadName()));
        }

        int lineNumber = 0;

        if (event.getCallerData().length > 0) {
            lineNumber = event.getCallerData()[0].getLineNumber();
        }

        log.append(String.format(" %s:%d: ", event.getLoggerName(), lineNumber));

        if (requireAlertLevel && event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            AbstractLoggingException exception = AbstractLoggingException.getFromLogEvent(event);

            if (exception != null) {
                log.append(String.format("[%s] ", exception.getAlertLevel().name()));
            }
        }

        log.append(event.getFormattedMessage());
        log.append(CoreConstants.LINE_SEPARATOR);

        return log.toString();
    }
}
