package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Objects.requireNonNull;

/**
 * Application Insights class to send custom telemetry to AppInsights.
 *
 * <code>// NO PMD</code> is for pmd rule to skip the check of any abstract methods being present in abstract class.
 */
@Aspect
public abstract class AbstractAppInsights { // NOPMD

    protected final TelemetryClient telemetry;

    protected AbstractAppInsights(TelemetryClient telemetry) {
        requireNonNull(
            telemetry.getContext().getInstrumentationKey(),
            "Missing APPINSIGHTS_INSTRUMENTATIONKEY environment variable"
        );
        telemetry.getContext().getComponent().setVersion(getClass().getPackage().getImplementationVersion());

        this.telemetry = telemetry;
    }

    @Pointcut("@annotation(dependency)")
    public void dependencyPointCut(Dependency dependency) {
        // point cut definition
    }

    @Around("dependencyPointCut(dependency)")
    public Object trackDependency(ProceedingJoinPoint joinPoint, Dependency dependency) throws Throwable {
        Instant start = Instant.now();

        try {
            Object proceed = joinPoint.proceed();

            telemetry.trackDependency(
                dependency.name(),
                dependency.command(),
                new Duration(MILLIS.between(start, Instant.now())),
                true
            );

            return proceed;
        } catch (Throwable exception) {
            telemetry.trackDependency(
                dependency.name(),
                dependency.command(),
                new Duration(MILLIS.between(start, Instant.now())),
                false
            );
            telemetry.trackException((Exception) exception);

            throw exception;
        }
    }
}
