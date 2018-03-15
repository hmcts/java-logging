package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.internal.util.PropertyHelper;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class VersionTest {

    @ClassRule
    public static EnvironmentVariables variables = new EnvironmentVariables();

    @Autowired
    private ApplicationContext context;

    @BeforeClass
    public static void setUp() {
        variables.set("APPINSIGHTS_INSTRUMENTATIONKEY", "some-key");
    }

    @Test
    public void shouldHaveSdkVersionSet() {
        String sdkVersion = PropertyHelper.getSdkVersionProperties().getProperty("version");

        assertThat(context
            .getBean(TelemetryClient.class)
            .getContext()
            .getInternal()
            .getSdkVersion()
        ).isEqualTo("java:" + sdkVersion);
    }
}
