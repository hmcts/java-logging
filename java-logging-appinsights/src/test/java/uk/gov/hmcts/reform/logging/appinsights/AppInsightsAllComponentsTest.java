package uk.gov.hmcts.reform.logging.appinsights;

import com.microsoft.applicationinsights.TelemetryConfiguration;
import com.microsoft.applicationinsights.web.spring.internal.InterceptorRegistry;
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
public class AppInsightsAllComponentsTest {

    @ClassRule
    public static EnvironmentVariables variables = new EnvironmentVariables();

    @Autowired
    private AbstractAppInsights insights;

    @Autowired
    private ApplicationContext context;

    @BeforeClass
    public static void setUp() {
        variables.set("APPLICATION_INSIGHTS_IKEY", "some-key");
    }

    @Test
    public void contextLoads() {
        assertThat(TelemetryConfiguration.getActive().getChannel().isDeveloperMode()).isFalse();
        assertThat(insights).isInstanceOf(SpringBootTestApplication.AppInsightsImp.class);
        assertThat(context.containsBean(InterceptorRegistry.class.getName())).isTrue();
        assertThat(context.containsBean("webRequestTrackingFilter")).isTrue();
        assertThat(context.containsBean("telemetryClient")).isTrue();
    }
}
