package uk.gov.hmcts.reform.logging.layout;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;
import uk.gov.hmcts.reform.logging.exception.InvalidExceptionImplementation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.NoSuchElementException;

public class ReformLoggingLayout extends LayoutBase<ILoggingEvent> {

    private Logger log = LoggerFactory.getLogger(ReformLoggingLayout.class);

    private Calendar calendar = Calendar.getInstance();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * By default require thread to be logged.
     */
    private boolean requireThread = true;

    /**
     * By default require an AlertLevel tag to be present for any exception log.
     */
    private boolean requireAlertLevel = true;

    public void setDateFormat(String format) {
        dateFormat = new SimpleDateFormat(format);
    }

    public void setRequireThread(boolean requireThread) {
        this.requireThread = requireThread;
    }

    public void setRequireAlertLevel(boolean requireAlertLevel) {
        this.requireAlertLevel = requireAlertLevel;
    }

    private void triggerBadImplementationLog(Throwable cause) {
        Throwable invalid = new InvalidExceptionImplementation("AlertLevel is mandatory as per configuration", cause);

        log.error("Bad implementation of '" + cause.getClass().getCanonicalName() + "' in use", invalid);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        AbstractLoggingException exception = null;

        if (requireAlertLevel && event.getLevel().isGreaterOrEqual(Level.ERROR)) {
            Throwable eventException = ((ThrowableProxy) event.getThrowableProxy()).getThrowable();

            try {
                exception = (AbstractLoggingException) eventException;
            } catch (ClassCastException e) {
                triggerBadImplementationLog(eventException);
            }
        }

        calendar.setTimeInMillis(event.getTimeStamp());
        String log = dateFormat.format(calendar.getTime());

        log += " " + String.format("%-5s", event.getLevel().levelStr);

        if (requireThread) {
            log += " [" + event.getThreadName() + "]";
        }

        int lineNumber = 0;

        try {
            lineNumber = event.getCallerData()[0].getLineNumber();
        } catch (NoSuchElementException e) {
            // do nothing
        }

        log += " " + event.getLoggerName() + ":" + lineNumber + ": ";

        if (requireAlertLevel && event.getLevel().isGreaterOrEqual(Level.ERROR) && exception != null) {
            log += "[" + exception.getAlertLevel().name() + "] ";
        }

        log += event.getFormattedMessage();
        log += CoreConstants.LINE_SEPARATOR;

        return log;
    }
}
