package org.specrunner.junit;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to sign the evaluator to ignore an attribute or method.
 * 
 * @author Thiago Santos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface ExpectedMessages {

    /**
     * List of messages.
     */
    String[] messages() default {};

    /**
     * ExpectedMessages messages should be sorted as specified? The sort flag.
     * Default is false.
     */
    boolean sorted() default false;
}
