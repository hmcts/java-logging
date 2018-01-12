package uk.gov.hmcts.reform.logging.layout;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import uk.gov.hmcts.reform.logging.exception.AbstractLoggingException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

public class ReformLoggingLayout extends LayoutBase<ILoggingEvent> {

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

    // can be added to config
    private static final int STACKTRACE_DEPTH = 3;

    /**
     * By default require thread to be logged.
     */
    private boolean requireThread = true;

    /**
     * By default require an AlertLevel tag to be present for any exception log.
     */
    private boolean requireAlertLevel = true;

    /**
     * By default require an Error Code to be present for any exception log.
     */
    private boolean requireErrorCode = true;

    public void setDateFormat(String format) {
        dateFormat = DateTimeFormatter.ofPattern(format);
    }

    public void setRequireThread(boolean requireThread) {
        this.requireThread = requireThread;
    }

    public void setRequireAlertLevel(boolean requireAlertLevel) {
        this.requireAlertLevel = requireAlertLevel;
    }

    public void setRequireErrorCode(boolean requireErrorCode) {
        this.requireErrorCode = requireErrorCode;
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        Instant instant = Instant.ofEpochMilli(event.getTimeStamp());
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        StringBuilder log = new StringBuilder(dateTime.format(dateFormat));

        log.append(String.format(" %-5s", event.getLevel().levelStr));

        if (requireThread) {
            log.append(String.format(" [%s]", event.getThreadName()));
        }

        int lineNumber = event.getCallerData().length > 0 ? event.getCallerData()[0].getLineNumber() : 0;

        log.append(String.format(" %s:%d: ", event.getLoggerName(), lineNumber));

        ThrowableProxy proxy = (ThrowableProxy) event.getThrowableProxy();

        if (requireAlertLevel || requireErrorCode) {
            appendExtraExceptionFlags(log, AbstractLoggingException.getFromThrowableProxy(proxy));
        }

        log.append(event.getFormattedMessage()).append(CoreConstants.LINE_SEPARATOR);

        appendStackTrace(log, proxy);

        if (proxy != null) {
            loopCauses(log, proxy, 0);
        }

        return log.toString();
    }

    private void appendExtraExceptionFlags(StringBuilder log, AbstractLoggingException exception) {
        if (exception != null && requireAlertLevel) {
            log.append(String.format("[%s] ", exception.getAlertLevel().name()));
        }

        if (exception != null && requireErrorCode) {
            log.append(String.format("%s. ", exception.getErrorCode()));
        }
    }

    private void appendStackTrace(StringBuilder log, ThrowableProxy proxy) {
        if (proxy != null) {
            Stream<StackTraceElementProxy> trace = Arrays.stream(proxy.getStackTraceElementProxyArray());

            trace.forEach(step -> {
                String string = step.toString();

                log.append(CoreConstants.TAB).append(string);

                ThrowableProxyUtil.subjoinPackagingData(log, step);

                log.append(CoreConstants.LINE_SEPARATOR);
            });

            trace.close();
        }
    }

    private void loopCauses(StringBuilder log, ThrowableProxy parentProxy, int depth) {
        ThrowableProxy cause = (ThrowableProxy) parentProxy.getCause();

        if (cause != null) {
            log.append(String.format(
                "Caused by: %s: %s",
                cause.getThrowable().getClass().getCanonicalName(),
                cause.getThrowable().getMessage()
            ));
            log.append(CoreConstants.LINE_SEPARATOR);
        }

        appendStackTrace(log, cause);

        if (cause != null && depth < STACKTRACE_DEPTH) {
            loopCauses(log, cause, depth + 1);
        }
    }
}
