package uk.gov.hmcts.reform.logging.appinsights.telemetry.initializers;

import com.microsoft.applicationinsights.extensibility.context.ContextTagKeys;
import com.microsoft.applicationinsights.telemetry.Telemetry;
import com.microsoft.applicationinsights.web.extensibility.initializers.WebTelemetryInitializerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudRoleNameInitializer extends WebTelemetryInitializerBase {
    private static final Logger logger = LoggerFactory.getLogger(CloudRoleNameInitializer.class);

    @Override
    protected void onInitializeTelemetry(Telemetry telemetry) {
        if (CloudRoleName.get() != null) {
            telemetry.getContext().getTags().put(ContextTagKeys.getKeys().getDeviceRoleName(), CloudRoleName.get());
        } else {
            logger.warn("Cloud role name not provided");
        }
    }
}
