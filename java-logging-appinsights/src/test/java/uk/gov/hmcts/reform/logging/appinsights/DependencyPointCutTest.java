package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.logging.appinsights.util.DependencyComponent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class DependencyPointCutTest {

    @ClassRule
    public static EnvironmentVariables variables = new EnvironmentVariables();

    @SpyBean
    private TelemetryClient telemetry;

    @Autowired
    private DependencyComponent dependency;

    @BeforeClass
    public static void setUp() {
        variables.set("APPINSIGHTS_INSTRUMENTATIONKEY", "some-key");
    }

    @Test
    public void shouldTrackDependencyCall() {
        dependency.method(false);
        Throwable exception = catchThrowable(() -> dependency.method(true));

        assertThat(exception).isNotNull();
        verify(telemetry).trackDependency(eq("Dependency"), eq("Call"), any(Duration.class), eq(true));
        verify(telemetry).trackDependency(eq("Dependency"), eq("Call"), any(Duration.class), eq(false));
    }
}
