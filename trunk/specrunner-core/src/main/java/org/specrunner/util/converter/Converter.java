/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.util.converter;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.specrunner.util.converter.impl.ConverterDefault;

/**
 * Annotation to sign the evaluator to ignore an attribute or method.
 * 
 * @author Thiago Santos
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD, PARAMETER })
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

    /**
     * The possible object of expression.
     */
    Class<?> resultType() default Object.class;
}
