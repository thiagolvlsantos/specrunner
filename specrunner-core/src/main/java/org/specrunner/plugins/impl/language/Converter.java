package org.specrunner.plugins.impl.language;

import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.impl.ConverterDefault;

/**
 * Annotation to sign the evaluator to ignore an attribute or method.
 * 
 * @author Thiago Santos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ PARAMETER })
public @interface Converter {

    /**
     * Converter name.
     */
    String name() default "";

    /**
     * Converter arguments.
     */
    String[] args() default {};

    /**
     * ExpectedMessages messages should be sorted as specified? The sort flag.
     * Default is false.
     */
    Class<? extends IConverter> type() default ConverterDefault.class;
}
