/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
     * Get full list annotation values of a given type. If annotation has an
     * attribute 'inherit' to declare if full hierarchy tree should be
     * considered, when 'inherit=false' bottom up search for annotations is
     * aborted.
     * 
     * @param type
     *            Object type.
     * @param annotationType
     *            A annotation type..
     * @param inherited
     *            If super type should be scanned also.
     * @return A list of annotation for the given type, in order, from top most
     *         class to bottom class.
     */
    public static <T extends Annotation> List<T> getAnnotations(Class<?> type, Class<T> annotationType, boolean inherited) {
        List<T> scan = new LinkedList<T>();
        try {
            Class<?> tmp = type;
            while (tmp != Object.class) {
                T local = tmp.getAnnotation(annotationType);
                if (local != null) {
                    scan.add(0, local);
                    try {
                        Method method = annotationType.getMethod("inherit");
                        if (!(boolean) method.invoke(local)) {
                            break;
                        }
                    } catch (NoSuchMethodException e) {
                        // move on
                    }
                }
                if (!inherited) {
                    break;
                }
                tmp = tmp.getSuperclass();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return scan;
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
            Set<Method> methods = new HashSet<Method>(getMethods(type));
            List<Method> ms = getAllDeclaredMethods(type);
            for (Method m : ms) {
                T c = m.getAnnotation(annotation);
                if (c != null) {
                    if (!Modifier.isPublic(m.getModifiers())) {
                        throw new IllegalArgumentException("Method is not public. " + m);
                    }
                    // select the most specific method to add.
                    if (methods.contains(m)) {
                        candidates.add(m);
                    }
                }
            }
        }
        return candidates;
    }

    /**
     * Get public methods of a type.
     * 
     * @param type
     *            A type.
     * @return Methods list.
     */
    public static List<Method> getMethods(Class<?> type) {
        if (type == null) {
            return new LinkedList<Method>();
        }
        return Arrays.asList(type.getMethods());
    }

    /**
     * Get all declared methods, from superclass to subclass in that order.
     * 
     * @param type
     *            The method type.
     * @return Methods list.
     */
    public static List<Method> getAllDeclaredMethods(Class<?> type) {
        List<Method> ms = new LinkedList<Method>();
        if (type == null) {
            return ms;
        }
        ms.addAll(getAllDeclaredMethods(type.getSuperclass()));
        ms.addAll(Arrays.asList(type.getDeclaredMethods()));
        return ms;
    }
}
