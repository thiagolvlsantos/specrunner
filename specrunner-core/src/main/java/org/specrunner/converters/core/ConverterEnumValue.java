/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
import org.specrunner.converters.IConverterReverse;

/**
 * Basic enumeration converter, from a text to the corresponding enumeration
 * item.
 * 
 * Three arguments are required:
 * <ul>
 * <li>The enumeration class type, or enumeration class name;</li>
 * <li>The method name to be used as reference for conversion, i.e. "name";</li>
 * <li>The method name to be used as result, i.e. "ordinal";</li>
 * </ul>
 * 
 * @author Thiago Santos.
 */
@SuppressWarnings("serial")
public class ConverterEnumValue extends ConverterEnum implements IConverterReverse {

    /**
     * Expected arguments size.
     */
    protected static final int ARG_SIZE = 3;

    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        if (obj == null) {
            return null;
        }
        if (args == null || args.length < ARG_SIZE) {
            throw new ConverterException("Converter requires three arguments: 0) the enum class type (class object or name); 1) enum method name passed to compare values; 2) the enum method to be called for result.");
        }
        return convertData(obj, args);
    }

    @Override
    public Object revert(Object obj, Object[] args) throws ConverterException {
        if (obj == null) {
            return null;
        }
        if (args == null || args.length < ARG_SIZE) {
            throw new ConverterException("Converter (revert mode) requires three arguments: 0) the enum class type (class object or name); 1) enum method name passed to compare values; 2) the enum method to be called for result.");
        }
        return convertData(obj, new Object[] { args[0], args[2], args[1] });
    }

    /**
     * Process a value.
     * 
     * @param obj
     *            Value.
     * @param args
     *            Arguments.
     * @return Processed value.
     * @throws ConverterException
     *             On processing errors.
     */
    protected Object convertData(Object obj, Object[] args) throws ConverterException {
        Class<? extends Enum<?>> clazz = recoverType(args[0]);
        Object result = null;
        try {
            result = recoverValue(obj, args[1], clazz);
        } catch (Exception e) {
            throw new ConverterException(e);
        }
        if (result != null) {
            try {
                String name = String.valueOf(args[2]);
                Method method = clazz.getMethod(name);
                result = method.invoke(result);
            } catch (Exception e) {
                throw new ConverterException(e);
            }
        }
        return result;
    }
}