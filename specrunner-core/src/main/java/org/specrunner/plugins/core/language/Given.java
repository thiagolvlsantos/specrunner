package org.specrunner.plugins.core.language;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

/**
 * Annotation for generic commands.
 * 
 * @author Thiago Santos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface Given {

    /**
     * Regular expression.
     */
    String value() default "";

    /**
     * Expression options. Values of <code>Pattern</code> constants. Default is
     * <code>Pattern.CASE_INSENSITIVE</code>.
     */
    int options() default Pattern.CASE_INSENSITIVE;
}
