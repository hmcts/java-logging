package uk.gov.hmcts.reform.logging.appinsights.util;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.logging.appinsights.Dependency;

@Component
public class DependencyComponent {

    public DependencyComponent() {
        // empty constructor
    }

    @Dependency(name = "Dependency", command = "Call")
    public int method(boolean fail) {
        if (fail) {
            throw new NumberFormatException();
        }

        return 42;
    }
}
