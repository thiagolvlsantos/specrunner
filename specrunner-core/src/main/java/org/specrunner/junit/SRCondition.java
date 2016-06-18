package org.specrunner.junit;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides scenario filters.
 * 
 * @author Thiago Santos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface SRCondition {

    Class<? extends IRunnerCondition> value() default ConditionTrue.class;

    boolean inherit() default true;
}
