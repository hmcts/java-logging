package uk.gov.hmcts.reform.logging.appinsights;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used against methods to be picked up by AppInsights aspect dependency advice.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependency {

    /**
     * Name of the dependency which is presented as a block in the application map in Azure AppInsights.
     *
     * @return Name of the dependency
     */
    String name();

    /**
     * Value which is presented in <code>Command</code> block in Azure AppInsights.
     *
     * @return Dependency command
     */
    String command();
}
