package org.specrunner.plugins.impl.language;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Expected message on test execution.
 * 
 * @author Thiago Santos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface Sentence {

    /**
     * List of messages.
     */
    String value() default "";
}
