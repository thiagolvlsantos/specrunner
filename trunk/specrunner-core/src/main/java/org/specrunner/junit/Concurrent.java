package org.specrunner.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Concurrent {

    /**
     * Default number of threads.
     */
    int THREADS = 5;

    /**
     * Number of threads.
     */
    int threads() default THREADS;
}