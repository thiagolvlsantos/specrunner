package org.specrunner.transformer;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Force loading of a class type.
 * 
 * @author Thiago Santos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD })
public @interface Transformer {

    /**
     * List of messages.
     */
    Class<? extends ITransformer> value();
}
