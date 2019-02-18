package uk.gov.hmcts.reform.logging.appinsights;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @BeforeClass
    public static void setUp() {
        variables.set("APPINSIGHTS_INSTRUMENTATIONKEY", "some-key");
    }

    @Test
    public void contextLoads() {
        assertThat(insights).isInstanceOf(SpringBootTestApplication.AppInsightsImp.class);
    }
}
