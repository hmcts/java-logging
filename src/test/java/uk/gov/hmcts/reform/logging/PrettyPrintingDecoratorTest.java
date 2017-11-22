package uk.gov.hmcts.reform.logging;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class PrettyPrintingDecoratorTest {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    private JsonGenerator jsonGenerator;

    private PrettyPrintingDecorator decorator;

    private String parameterValue;
    private int invocationsCount;

    @Parameterized.Parameters(name = "Is pretty printing be enabled for \"{0}\"? {2}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "", false },
                { "false", false },
                { "fdjksfdks", false },
                { "true", true }
        });
    }

    public PrettyPrintingDecoratorTest(String parameterValue, boolean expectedPrettyPrint) {
        this.parameterValue = parameterValue;
        this.invocationsCount = expectedPrettyPrint ? 1 : 0;
        jsonGenerator = mock(JsonGenerator.class);
        decorator = new PrettyPrintingDecorator();
    }

    @Test
    public void execute() {
        environmentVariables.set(PrettyPrintingDecorator.JSON_CONSOLE_PRETTY_PRINT, parameterValue);
        decorator.decorate(jsonGenerator);
        verify(jsonGenerator, times(invocationsCount)).useDefaultPrettyPrinter();
    }

}
