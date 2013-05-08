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
import java.lang.reflect.Modifier;

import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.SpecRunnerServices;
import org.specrunner.parameters.IAccess;
import org.specrunner.parameters.IAccessFactory;
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
    private ICache<String, IAccess> cache;

    @Override
    public IAccess newAccess(Object target, String name) {
        if (target == null) {
            return null;
        }
        Class<?> c = target.getClass();
        String key = c.getName() + "." + name;
        if (cache == null) {
            cache = SpecRunnerServices.get(ICacheFactory.class).newCache(AccessFactoryImpl.class.getSimpleName());
        }
        if (cache.contains(key)) {
            return cache.get(key);
        }
        IAccess access = null;
        try {
            PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(target, name);
            if (pd != null) {
                access = new AccessImpl(pd);
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        if (access == null) {
            try {
                Field f = c.getDeclaredField(name);
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
                    UtilLog.LOG.trace(e.getMessage(), e);
                }
            }
        }
        if (access == null) {
            try {
                Method m = null;
                for (Method i : c.getDeclaredMethods()) {
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
        }
        cache.put(key, access);
        return access;
    }
}