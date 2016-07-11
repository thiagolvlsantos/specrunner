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
package org.specrunner.parameters.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.parameters.IAccess;
import org.specrunner.parameters.IAccessFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Default implementation of <code>IAccessFactory</code>.
 * 
 * @author Thiago Santos
 * 
 */
public class AccessFactoryImpl implements IAccessFactory {

    /**
     * Cache for access information.
     */
    protected ICache<String, IAccess> cache;

    @Override
    public IAccess newAccess(Object target, String name) {
        if (target == null) {
            return null;
        }
        Class<?> c = target.getClass();
        String key = c.getName() + "." + name;
        if (cache == null) {
            cache = SRServices.get(ICacheFactory.class).newCache(AccessFactoryImpl.class.getSimpleName());
        }
        if (cache.contains(key)) {
            return cache.get(key);
        }
        IAccess access = lookupBean(target, name);
        if (access == null) {
            access = lookupField(c, name);
        }
        if (access == null) {
            access = lookupMethod(c, name);
        }
        cache.put(key, access);
        return access;
    }

    /**
     * Lookup for a bean property.
     * 
     * @param target
     *            The object instance.
     * @param name
     *            The feature name.
     * @return The access object, if property exists, null, otherwise.
     */
    protected IAccess lookupBean(Object target, String name) {
        IAccess access = null;
        try {
            PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(target, name);
            if (pd != null) {
                if (pd.getReadMethod() != null) {
                    access = new AccessImpl(pd);
                }
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        return access;
    }

    /**
     * Lookup for a public attribute.
     * 
     * @param clazz
     *            The class.
     * @param name
     *            The feature name.
     * @return The access object, if the field exists, null, otherwise.
     */
    protected IAccess lookupField(Class<?> clazz, String name) {
        if ("class".equals(name)) {
            return null;
        }
        IAccess access = null;
        try {
            Field f = clazz.getDeclaredField(name);
            if (f != null) {
                if (Modifier.isPublic(f.getModifiers())) {
                    access = new AccessImpl(f);
                } else {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Field '" + f.getName() + "' is not accessible. " + f);
                    }
                }
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace("Field '" + name + "' for class '" + clazz.getName() + "' not found. Error: " + e.getClass().getName() + ", Message: " + e.getMessage());
            }
        }
        return access;
    }

    /**
     * Lookup for public method.
     * 
     * @param clazz
     *            The class.
     * @param name
     *            The feature name.
     * @return The access object, if the method exists, null, otherwise.
     */
    protected IAccess lookupMethod(Class<?> clazz, String name) {
        IAccess access = null;
        try {
            Method m = null;
            for (Method i : clazz.getDeclaredMethods()) {
                if (i.getName().equals(name)) {
                    if (Modifier.isPublic(i.getModifiers())) {
                        m = i;
                    } else {
                        if (UtilLog.LOG.isTraceEnabled()) {
                            UtilLog.LOG.trace("Method '" + i.getName() + "' is not accessible. " + i);
                        }
                    }
                    break;
                }
            }
            if (m != null) {
                access = new AccessImpl(m);
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        return access;
    }

    @Override
    public Object getProperty(Object source, String str) throws PluginException {
        Object bean = source;
        IFeatureManager fm = SRServices.getFeatureManager();
        Boolean acceptNullPath = (Boolean) fm.get(FEATURE_PROPERTY_ACCEPT_NULL_PATH, DEFAULT_PROPERTY_ACCEPT_NULL_PATH);
        Boolean invalidPathAsNull = (Boolean) fm.get(FEATURE_PROPERTY_INVALID_PATH_AS_NULL, DEFAULT_PROPERTY_INVALID_PATH_AS_NULL);
        try {
            StringTokenizer st = new StringTokenizer(str, ".");
            StringBuilder path = new StringBuilder();
            while (bean != null && st.hasMoreTokens()) {
                String part = st.nextToken();
                IAccess access = newAccess(bean, part);
                if ((access == null || !access.hasFeature()) && invalidPathAsNull) {
                    bean = null;
                    break;
                }
                bean = access.get(bean, part);
                path.append('.');
                path.append(part);
                if (bean == null && !acceptNullPath && st.hasMoreElements()) {
                    throw new PluginException("Invalid null value for part '" + path + "' of property '" + str + "'.");
                }
            }
            return bean;
        } catch (PluginException e) {
            throw e;
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }
}
