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
package org.specrunner.parameters.core;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.converters.UtilConverter;
import org.specrunner.parameters.IAccess;
import org.specrunner.plugins.PluginException;

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
    protected Field field;
    /**
     * A bean access.
     */
    protected PropertyDescriptor property;
    /**
     * A method access.
     */
    protected Method method;

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
            field.set(target, prepare(args[0]));
        } else if (property != null) {
            PropertyUtils.setProperty(target, name, prepare(args[0]));
        } else if (method != null) {
            method.invoke(target, prepare(args));
        }
    }

    /**
     * Prepare an array of arguments. In this case the only changed value is the
     * first one.
     * 
     * @param args
     *            The arguments.
     * @return The array modified.
     * @throws PluginException
     *             On preparation errors.
     */
    protected Object[] prepare(Object[] args) throws PluginException {
        if (args == null) {
            return null;
        }
        if (args.length == 0) {
            return args;
        }
        args[0] = prepare(args[0]);
        return args;
    }

    /**
     * Prepare argument for set.
     * 
     * @param object
     *            The argument object.
     * @return The most appropriate value for the access type.
     * @throws PluginException
     *             On preparation errors.
     */
    protected Object prepare(Object object) throws PluginException {
        if (object == null) {
            return null;
        }
        Class<?> type = null;
        Annotation[] annotations = null;
        if (field != null) {
            type = field.getType();
            annotations = field.getAnnotations();
        }
        if (property != null) {
            type = property.getPropertyType();
            annotations = getMethodAnnotations(property.getWriteMethod());
        }
        if (method != null) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            type = parameterTypes.length > 0 ? parameterTypes[0] : null;
            annotations = getMethodAnnotations(method);
        }
        if (type != null && !type.isInstance(object)) {
            object = UtilConverter.prepareArgument(null, toString(), annotations, type, object);
        }
        return object;
    }

    /**
     * Get annotation for methods.
     * 
     * @param method
     *            The write method.
     * @return The annotations.
     */
    protected Annotation[] getMethodAnnotations(Method method) {
        Annotation[] annotations = method.getAnnotations();
        if (UtilConverter.getConverter(annotations) == null) {
            annotations = method.getParameterAnnotations()[0];
        }
        return annotations;
    }

    @Override
    public Object get(Object target, String name, Object... args) throws Exception {
        if (target == null) {
            return null;
        }
        if (field != null) {
            return field.get(target);
        } else if (property != null) {
            return PropertyUtils.getProperty(target, name);
        } else if (method != null) {
            return method.invoke(target, args);
        }
        return null;
    }

    @Override
    public boolean valid(Object target, String name, Object... args) {
        Class<?>[] types = accepted(target, name, args);
        boolean valid = true;
        for (int i = 0; valid && args != null && i < types.length && i < args.length; i++) {
            valid = valid && (args[i] == null || types[i].isAssignableFrom(args[i].getClass()));
        }
        return valid;
    }

    @Override
    public Class<?> expected(Object target, String name, Object... args) {
        if (field != null) {
            return field.getType();
        } else if (property != null) {
            return property.getPropertyType();
        } else if (method != null) {
            return method.getReturnType();
        }
        return null;
    }

    @Override
    public Class<?>[] accepted(Object target, String name, Object... args) {
        if (field != null) {
            return new Class<?>[] { field.getType() };
        } else if (property != null) {
            return new Class<?>[] { property.getPropertyType() };
        } else if (method != null) {
            return method.getParameterTypes();
        }
        return new Class<?>[] {};
    }

    @Override
    public boolean hasFeature() {
        return field != null || property != null || method != null;
    }

    @Override
    public Annotation[] getAnnotations() {
        if (field != null) {
            return field.getAnnotations();
        } else if (property != null) {
            Method writeMethod = property.getWriteMethod();
            if (writeMethod != null) {
                return writeMethod.getAnnotations();
            }
        } else if (method != null) {
            return method.getAnnotations();
        }
        return new Annotation[] {};
    }

    @Override
    public String toString() {
        return "Access: " + (field != null ? field : (property != null ? (property.getPropertyType() + ":" + property.getName() + ":" + property.getReadMethod() + ":" + property.getWriteMethod()) : (method != null ? method : null)));
    }
}
