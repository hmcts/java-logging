package uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers;

import com.microsoft.applicationinsights.extensibility.ContextInitializer;
import com.microsoft.applicationinsights.telemetry.TelemetryContext;
import uk.gov.hmcts.reform.logging.appinsights.SpringApplicationName;

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
