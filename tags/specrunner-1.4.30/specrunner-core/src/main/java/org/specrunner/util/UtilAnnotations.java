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
package org.specrunner.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility Annotations class.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilAnnotations {

    /**
     * Hidden constructor.
     */
    private UtilAnnotations() {
    }

    /**
     * Get annotations of a given object.
     * 
     * @param <T>
     *            Annotation type.
     * @param instance
     *            An object instance.
     * @param annotation
     *            The annotation type.
     * @return A list of methods with that annotation.
     */
    public static <T extends Annotation> List<Method> getAnnotatedMethods(Object instance, Class<T> annotation) {
        return getAnnotatedMethods(instance != null ? instance.getClass() : null, annotation);
    }

    /**
     * Get annotations of a given type.
     * 
     * @param <T>
     *            Annotation type.
     * @param type
     *            A class type.
     * @param annotation
     *            The annotation type.
     * @return A list of methods with that annotation.
     */
    public static <T extends Annotation> List<Method> getAnnotatedMethods(Class<?> type, Class<T> annotation) {
        List<Method> candidates = new LinkedList<Method>();
        if (type != null) {
            Method[] ms = type.getMethods();
            for (Method m : ms) {
                T c = m.getAnnotation(annotation);
                if (c != null) {
                    candidates.add(m);
                }
            }
        }
        return candidates;
    }
}