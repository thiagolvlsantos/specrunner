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

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginNamed;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.util.UtilEvaluator;

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
        boolean error = false;
        if (bean instanceof String) {
            try {
                bean = Class.forName((String) bean).newInstance();
            } catch (InstantiationException e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
                error = true;
            } catch (IllegalAccessException e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
                error = true;
            } catch (ClassNotFoundException e) {
                result.addResult(Failure.INSTANCE, context.peek(), e);
                error = true;
            }
        }
        if (!error) {
            context.saveStrict(UtilEvaluator.asVariable(BEAN_NAME), bean);
        }
        return ENext.DEEP;
    }
}