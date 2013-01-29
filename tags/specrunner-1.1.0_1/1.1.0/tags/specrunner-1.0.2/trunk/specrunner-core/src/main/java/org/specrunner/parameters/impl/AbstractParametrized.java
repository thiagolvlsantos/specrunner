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
package org.specrunner.parameters.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.specrunner.parameters.IParametrized;
import org.specrunner.util.UtilLog;

/**
 * Generic implementation of a parameter sensible object.
 * 
 * @author Thiago Santos
 * 
 */
public class AbstractParametrized implements IParametrized {

    /**
     * Set of valid parameters.
     */
    protected Map<String, Object> parameters = new HashMap<String, Object>();
    /**
     * Set of all parameters, valid or not.
     */
    protected Map<String, Object> allParameters = new HashMap<String, Object>();

    @Override
    public Object getParameter(String name) {
        try {
            return BeanUtils.getProperty(this, name);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void setParameter(String name, Object value) {
        allParameters.put(name, value);
        try {
            if (hasParameter(name)) {
                BeanUtils.setProperty(this, name, value);
                parameters.put(name, value);
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    @Override
    public boolean hasParameter(String name) {
        boolean result = false;
        try {
            PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(this, name);
            result = pd != null;
        } catch (IllegalAccessException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        } catch (InvocationTargetException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        } catch (NoSuchMethodException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Set parameters map.
     * 
     * @param parameters
     *            The map.
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getAllParameters() {
        return allParameters;
    }

    /**
     * Set of all parameters map.
     * 
     * @param allParameters
     *            The map.
     */
    public void setAllParameters(Map<String, Object> allParameters) {
        this.allParameters = allParameters;
    }
}