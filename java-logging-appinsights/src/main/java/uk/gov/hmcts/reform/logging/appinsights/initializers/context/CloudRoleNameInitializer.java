package uk.gov.hmcts.reform.logging.appinsights.initializers.context;

import com.microsoft.applicationinsights.extensibility.ContextInitializer;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import uk.gov.hmcts.reform.logging.appinsights.SpringApplicationName;

/**
 * Context initializer which sets device role name for Microsoft cloud application.
 */
public class CloudRoleNameInitializer implements ContextInitializer {

    @Override
    public void initialize(TelemetryContext telemetryContext) {
        if (SpringApplicationName.get() == null) {
            throw new IllegalStateException("Property 'name' is not set. Check SpringApplicationName for set up");
        } else {
            telemetryContext.getDevice().setRoleName(SpringApplicationName.get());
        }
    }
}
