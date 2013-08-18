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
package org.specrunner.plugins.impl.var;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginNamed;
import org.specrunner.plugins.impl.include.PluginImport;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * A bean plugin.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginBean extends AbstractPluginNamed {

    /**
     * The bean object name on context.
     */
    public static final String BEAN_NAME = "$BEAN";

    /**
     * Bean instance.
     */
    private Object bean;

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    /**
     * The plugin bean.
     * 
     * @return The bean.
     */
    public Object getBean() {
        return bean;
    }

    /**
     * Set the bean object.
     * 
     * @param bean
     *            The bean object.
     */
    public void setBean(Object bean) {
        this.bean = bean;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (bean instanceof String) {
            String old = (String) bean;
            List<Exception> errors = new LinkedList<Exception>();
            bean = create(old, errors);
            List<String> packages = PluginImport.getPackages(context);
            if (bean == null) {
                for (String pkg : packages) {
                    bean = create(pkg + "." + old, errors);
                    if (bean != null) {
                        break;
                    }
                }
            }
            if (bean == null && !errors.isEmpty()) {
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Class '" + old + "' not found on classpath, and does not belong to any package in list: " + packages));
            }
        }
        if (bean != null) {
            context.saveStrict(UtilEvaluator.asVariable(BEAN_NAME), bean);
        }
        return ENext.DEEP;
    }

    /**
     * Try to create the object instance.
     * 
     * @param type
     *            The class name (fully qualified).
     * @param errors
     *            The error list.
     * @return A new object of the given type.
     */
    protected Object create(String type, List<Exception> errors) {
        Object result = null;
        try {
            result = Class.forName(type).newInstance();
        } catch (InstantiationException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
            errors.add(e);
        } catch (IllegalAccessException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
            errors.add(e);
        } catch (ClassNotFoundException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
            errors.add(e);
        }
        return result;
    }

    /**
     * Get the current bean object.
     * 
     * @param context
     *            The context.
     * @return The bean object, if found, null, otherwise.
     */
    public static Object getBean(IContext context) {
        return context.getByName(UtilEvaluator.asVariable(BEAN_NAME));
    }
}