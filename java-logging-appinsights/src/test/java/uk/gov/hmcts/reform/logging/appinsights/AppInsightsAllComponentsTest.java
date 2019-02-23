package uk.gov.hmcts.reform.logging.appinsights;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
    "azure.applicationinsights.instrumentationkey=some-key"
})
public class AppInsightsAllComponentsTest {

    @Autowired
    private AbstractAppInsights insights;

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        assertThat(insights).isInstanceOf(SpringBootTestApplication.AppInsightsImp.class);
        assertThat(context.containsBean("contextVersionInitializer")).isTrue();
        assertThat(context.containsBean("sequencePropertyInitializer")).isTrue();
        assertThat(context.containsBean("timestampPropertyInitializer")).isTrue();
        assertThat(context.containsBean("webSyntheticRequestTelemetryInitializer")).isTrue();

    }
}
