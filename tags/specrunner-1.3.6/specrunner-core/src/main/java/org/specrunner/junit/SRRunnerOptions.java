package org.specrunner.junit;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Runner parameters options.
 * 
 * @author Thiago Santos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
@Inherited
public @interface SRRunnerOptions {

    /**
     * Pipeline specification file.
     */
    String pipeline() default "specrunner.xml";
}