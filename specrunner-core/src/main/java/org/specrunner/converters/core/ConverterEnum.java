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
package org.specrunner.converters.core;

import java.lang.reflect.Method;

import org.specrunner.converters.ConverterException;

/**
 * Basic enumeration converter, from a text to the corresponding enumeration
 * item.
 * 
 * Two arguments are required:
 * <ul>
 * <li>The enumeration class type, or enumeration class name;</li>
 * <li>The method name to be used as reference for convertion, i.e. "ordinal".
 * </li>
 * </ul>
 * 
 * @author Thiago Santos.
 */
@SuppressWarnings("serial")
public class ConverterEnum extends ConverterNotNullNotEmpty {

    /**
     * Expected arguments size.
     */
    protected static final int ARG_SIZE = 2;

    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        if (obj == null) {
            return null;
        }
        if (args == null || args.length < ARG_SIZE) {
            throw new ConverterException("Converter requires two arguments: 0) the enum class type (class object or name); 1) enum method name passed to compare values.");
        }
        return convertData(obj, args);
    }

    protected Object convertData(Object obj, Object[] args) throws ConverterException {
        Class<? extends Enum<?>> clazz = recoverType(args[0]);
        Object result = null;
        try {
            result = recoverValue(obj, args[1], clazz);
        } catch (Exception e) {
            throw new ConverterException(e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends Enum<?>> recoverType(Object type) throws ConverterException {
        Class<?> clazz = null;
        try {
            if (type instanceof Class) {
                clazz = (Class<?>) type;
            } else {
                clazz = Class.forName(String.valueOf(type));
            }
        } catch (Exception e) {
            throw new ConverterException(e);
        }
        if (!clazz.isEnum()) {
            throw new ConverterException("Class '" + clazz.getName() + "' is not an enumeration.");
        }
        return (Class<? extends Enum<?>>) clazz;
    }

    protected Object recoverValue(Object obj, Object methodName, Class<? extends Enum<?>> enumType) throws Exception {
        Object result = null;
        String name = String.valueOf(methodName);
        Method method = enumType.getMethod(name);
        String current = String.valueOf(obj);
        Enum<?>[] values = enumType.getEnumConstants();
        for (Enum<?> e : values) {
            Object base = method.invoke(e);
            if (current.equalsIgnoreCase(String.valueOf(base))) {
                result = e;
                break;
            }
        }
        return result;
    }
}
