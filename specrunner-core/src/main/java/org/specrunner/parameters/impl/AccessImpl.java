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
package org.specrunner.parameters.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.BeanUtils;
import org.specrunner.parameters.IAccess;

/**
 * Default implementation of <code>IAccess</code>.
 * 
 * @author Thiago Santos
 * 
 */
public class AccessImpl implements IAccess {

    /**
     * A filed access.
     */
    private Field field;
    /**
     * A bean access.
     */
    private PropertyDescriptor property;
    /**
     * A method access.
     */
    private Method method;

    /**
     * Basic filed access constructor.
     * 
     * @param field
     *            The field.
     */
    public AccessImpl(Field field) {
        this.field = field;
    }

    /**
     * Basic bean access constructor.
     * 
     * @param property
     *            The bean property.
     */
    public AccessImpl(PropertyDescriptor property) {
        this.property = property;
    }

    /**
     * Basic method access constructor.
     * 
     * @param method
     *            The method.
     */
    public AccessImpl(Method method) {
        this.method = method;
    }

    @Override
    public void set(Object target, String name, Object... args) throws Exception {
        if (target == null) {
            return;
        }
        if (field != null) {
            field.set(target, args[0]);
        } else if (property != null) {
            BeanUtils.setProperty(target, name, args[0]);
        } else if (method != null) {
            method.invoke(target, args);
        }
    }

    @Override
    public Object get(Object target, String name, Object... args) throws Exception {
        if (target == null) {
            return null;
        }
        if (field != null) {
            return field.get(target);
        } else if (property != null) {
            return BeanUtils.getProperty(target, name);
        } else if (method != null) {
            return method.invoke(target, args);
        }
        return null;
    }

    @Override
    public boolean valid(Object target, String name, Object... args) {
        Class<?>[] types = expected(target, name, args);
        boolean valid = true;
        for (int i = 0; valid && args != null && i < types.length && i < args.length; i++) {
            valid = valid && (args[i] == null || types[i].isAssignableFrom(args[i].getClass()));
        }
        return valid;
    }

    @Override
    public Class<?>[] expected(Object target, String name, Object... args) {
        if (field != null) {
            return new Class<?>[] { field.getType() };
        } else if (property != null) {
            return new Class<?>[] { property.getPropertyType() };
        } else if (method != null) {
            return method.getParameterTypes();
        }
        return new Class<?>[] {};
    }
}
