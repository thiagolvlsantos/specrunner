/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.features.impl;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.features.FeatureManagerException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.util.UtilLog;

/**
 * Default feature manager implementation.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class FeatureManagerImpl extends HashMap<String, Object> implements IFeatureManager {

    /**
     * Set of overrides information.
     */
    protected Map<String, Boolean> overrides = new HashMap<String, Boolean>();
    /**
     * The current configuration.
     */
    protected IConfiguration configuration;

    @Override
    public Object put(String key, Object value) {
        add(key, value);
        return super.put(key, value);
    }

    @Override
    public IFeatureManager add(String key, Object value) {
        add(key, value, true);
        return this;
    }

    @Override
    public IFeatureManager add(String key, Object value, boolean override) {
        super.put(key, value);
        overrides.put(key, override);
        return this;
    }

    @Override
    public Object get(Object key) {
        // search local configuration
        Object result = getConfiguration() != null ? getConfiguration().get(key) : null;
        if (result == null) {
            // then search global configuration.
            result = super.get(key);
        }
        return result;
    }

    @Override
    public void set(String feature, Object target) {
        try {
            setStrict(feature, target);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public void setStrict(String feature, Object target) throws FeatureManagerException {
        Object obj = get(feature);
        if (obj != null) {
            int pos = feature.lastIndexOf('.');
            if (pos < 0) {
                throw new FeatureManagerException("A feature should always end with a attribute name. i.e. '<any class name>.pause', current value:'" + feature + "'.");
            }
            String field = feature.substring(Math.min(pos + 1, feature.length())).trim();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Trying to set feature '" + feature + "' with value '" + obj + "' to object '" + target + "' on field '" + field + "'.");
            }
            PropertyDescriptor pd;
            try {
                pd = PropertyUtils.getPropertyDescriptor(target, field);
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("PropertyDescriptor(" + target + "." + field + ") not found: ignoring attempt.", e);
                }
                return;
            }
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Property descriptor '" + pd + "'.");
            }
            Class<?> expectedType = pd.getPropertyType();
            if (!expectedType.isAssignableFrom(obj.getClass())) {
                throw new FeatureManagerException("Object associated to " + feature + " is not a " + expectedType + ", current feature value '" + obj + "' is " + obj.getClass() + ".");
            }
            try {
                Boolean override = overrides.get(feature);
                if (override != null && !override) {
                    Object old = BeanUtils.getProperty(obj, field);
                    if (old == null) {
                        BeanUtils.setProperty(target, field, obj);
                    }
                } else {
                    BeanUtils.setProperty(target, field, obj);
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Feature '" + feature + "' set to object '" + target + ", current value is " + obj + ".");
                }
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new FeatureManagerException(e);
            }
        }
    }

    /**
     * Gets the configuration.
     * 
     * @return The configuration.
     */
    public IConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(IConfiguration configuration) {
        this.configuration = configuration;
    }
}