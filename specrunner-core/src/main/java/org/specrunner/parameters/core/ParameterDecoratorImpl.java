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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.expressions.Late;
import org.specrunner.expressions.Unsilent;
import org.specrunner.parameters.DontEval;
import org.specrunner.parameters.IAccess;
import org.specrunner.parameters.IAccessFactory;
import org.specrunner.parameters.IParameterDecorator;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.expression.UtilExpression;

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
    protected Object decorated;
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
    public boolean isEval(String name) {
        return is(name, INVERT_FLAG, DontEval.class);
    }

    @Override
    public boolean isSilent(String name) {
        return is(name, SILENT_FLAG, Unsilent.class);
    }

    @Override
    public boolean isLate(String name) {
        boolean invert = name.contains(LATE_FLAG);
        name = clear(name);
        if (hasParameter(name)) {
            IAccess access = checked.get(name);
            if (access != null && access.hasFeature()) {
                boolean late = hasAnnotation(access, Late.class);
                if (invert) {
                    late = !late;
                }
                return late;
            }
        }
        return false;
    }

    /**
     * Combine flag and annotation information.
     * 
     * @param name
     *            The feature name.
     * @param flag
     *            The flag name.
     * @param annotation
     *            The annotation.
     * @return true, if conditions as satisfied, false, otherwise.
     */
    protected boolean is(String name, String flag, Class<? extends Annotation> annotation) {
        boolean invert = name.contains(flag);
        name = clear(name);
        if (hasParameter(name)) {
            IAccess access = checked.get(name);
            if (access != null && access.hasFeature()) {
                boolean action = !hasAnnotation(access, annotation);
                if (invert) {
                    action = !action;
                }
                return action;
            }
        }
        return true;
    }

    @Override
    public String clear(String name) {
        return name.replace(INVERT_FLAG, "").replace(SILENT_FLAG, "").replace(LATE_FLAG, "");
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
    public Object setParameter(String name, Object value, IContext context) throws Exception {
        Object newValue = value;
        String old = name;
        name = clear(name);
        if (hasParameter(name)) {
            try {
                IAccess s = checked.get(name);
                newValue = prepareValue(value, context, isEval(old), isSilent(old));
                s.set(decorated, name, newValue);
                parameters.put(name, newValue);
            } catch (Exception e) {
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace(e.getMessage(), e);
                }
                if (!isSilent(old)) {
                    throw e;
                }
            }
        }
        allParameters.put(name, newValue);
        return newValue;
    }

    /**
     * Prepare the value to set.
     * 
     * @param value
     *            The value.
     * @param context
     *            The context.
     * @param eval
     *            True, to evalualte, false, otherwise.
     * @param silent
     *            True, to silent evaluation, false, otherwise.
     * @return The value after preparation.
     * @throws PluginException
     *             On processing errors.
     */
    protected Object prepareValue(Object value, IContext context, boolean eval, boolean silent) throws PluginException {
        return eval ? UtilExpression.evaluate(String.valueOf(value), context, silent) : value;
    }

    /**
     * Check if <code>@DontEval</code> annotation is present in feature.
     * 
     * @param s
     *            The feature access.
     * @param an
     *            Annotation type.
     * @return true, if annotation present, false, otherwise.
     */
    protected boolean hasAnnotation(IAccess s, Class<? extends Annotation> an) {
        for (Annotation a : s.getAnnotations()) {
            if (a.annotationType() == an) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasParameter(String name) {
        IAccess access = checked.get(name);
        if (access == null) {
            access = SRServices.get(IAccessFactory.class).newAccess(decorated, name);
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
