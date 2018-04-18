/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.parameters;

import java.lang.annotation.Annotation;

/**
 * Abstraction for object feature access. The features can be a public
 * attribute, a bean property, or a single method.
 * 
 * @author Thiago Santos
 * 
 */
public interface IAccess {

    /**
     * Set the value to a given feature.
     * 
     * @param target
     *            The target object.
     * @param name
     *            The feature reference.
     * @param args
     *            The arguments.
     * @throws Exception
     *             On processing errors.
     */
    void set(Object target, String name, Object... args) throws Exception;

    /**
     * Get the feature value.
     * 
     * @param target
     *            The target object.
     * @param name
     *            The feature reference.
     * @param args
     *            The arguments, if any.
     * @return The value of feature.
     * @throws Exception
     *             On processing errors.
     */
    Object get(Object target, String name, Object... args) throws Exception;

    /**
     * Check if the given arguments are compatible with the feature named.
     * 
     * @param target
     *            The target object.
     * @param name
     *            The feature reference.
     * @param args
     *            The arguments.
     * @return true, if compatible, false, otherwise.
     */
    boolean valid(Object target, String name, Object... args);

    /**
     * Get the expected type(s) for a feature.
     * 
     * @param target
     *            The target object.
     * @param name
     *            The feature reference.
     * @param args
     *            The arguments.
     * @return Type encapsulated by access.
     */
    Class<?> expected(Object target, String name, Object... args);

    /**
     * Get the accepted type(s) for a feature assignment or arguments.
     * 
     * @param target
     *            The target object.
     * @param name
     *            The feature reference.
     * @param args
     *            The arguments.
     * @return true, if compatible, false, otherwise.
     */
    Class<?>[] accepted(Object target, String name, Object... args);

    /**
     * Check if access has feature associated or null.
     * 
     * @return true, for any associated feature, false, otherwise.
     */
    boolean hasFeature();

    /**
     * Get the annotation information of a given access.
     * 
     * @return The list of annotations associated to the access.
     */
    Annotation[] getAnnotations();
}
