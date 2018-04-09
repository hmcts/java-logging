package uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers;

import com.microsoft.applicationinsights.extensibility.context.ContextTagKeys;
import com.microsoft.applicationinsights.telemetry.Telemetry;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebTelemetryInitializerBase;

public class CloudRoleNameInitializer extends WebTelemetryInitializerBase {
    @Override
    protected void onInitializeTelemetry(Telemetry telemetry) {
        if (CloudRoleName.get() != null) {
            telemetry.getContext().getTags().put(ContextTagKeys.getKeys().getDeviceRoleName(),
                CloudRoleName.get());
        } else {
            System.out.println("It's null");
        }
    }
}
