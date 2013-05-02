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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.parameters.DontEval;
import org.specrunner.parameters.IAccess;
import org.specrunner.parameters.IAccessFactory;
import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Generic implementation of a parameter sensible object.
 * 
 * @author Thiago Santos
 * 
 */
public class ParameterDecoratorImpl implements IParameterDecorator {

    /**
     * The object to be parameterized.
     */
    private Object decorated;
    /**
     * Hold information of already checked attributes.
     */
    protected Map<String, IAccess> checked = new HashMap<String, IAccess>();
    /**
     * Set of valid parameters.
     */
    protected Map<String, Object> parameters = new HashMap<String, Object>();
    /**
     * Set of all parameters, valid or not.
     */
    protected Map<String, Object> allParameters = new HashMap<String, Object>();

    @Override
    public Object getDecorated() {
        return decorated;
    }

    @Override
    public void setDecorated(Object decorated) {
        this.decorated = decorated;
    }

    @Override
    public Object getParameter(String name) {
        if (hasParameter(name)) {
            try {
                IAccess s = checked.get(name);
                return s.get(decorated, name, allParameters.get(name));
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    @Override
    public Object setParameter(String name, Object value, IContext context) {
        Object newValue = value;
        if (hasParameter(name)) {
            try {
                IAccess s = checked.get(name);
                newValue = prepareValue(s, value, context);
                s.set(decorated, name, newValue);
                parameters.put(name, newValue);
            } catch (Exception e) {
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace(e.getMessage(), e);
                }
            }
        }
        allParameters.put(name, newValue);
        return newValue;
    }

    /**
     * Prepare the value to set.
     * 
     * @param s
     *            The access information object.
     * @param value
     *            The value.
     * @param context
     *            The context.
     * @return The value after preparation.
     * @throws PluginException
     *             On processing errors.
     */
    private Object prepareValue(IAccess s, Object value, IContext context) throws PluginException {
        boolean eval = true;
        for (Annotation a : s.getAnnotations()) {
            if (a.annotationType() == DontEval.class) {
                eval = false;
                break;
            }
        }
        if (eval) {
            value = UtilEvaluator.evaluate(String.valueOf(value), context);
        }
        return value;
    }

    @Override
    public boolean hasParameter(String name) {
        IAccess access = checked.get(name);
        if (access == null) {
            access = SpecRunnerServices.get(IAccessFactory.class).newAccess(decorated, name);
            checked.put(name, access);
        }
        return access != null;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, Object> getAllParameters() {
        return allParameters;
    }

    @Override
    public void setAllParameters(Map<String, Object> allParameters) {
        this.allParameters = allParameters;
    }
}