/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.junit;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.specrunner.listeners.IScenarioListener;

/**
 * Provides scenario listeners.
 * 
 * @author Thiago Santos
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface SRScenarioListeners {

    /**
     * List of scenario classes.
     */
    Class<? extends IScenarioListener>[] value() default {};

    /**
     * Inherit listeners from superclass.
     */
    boolean inheritListeners() default true;
}
