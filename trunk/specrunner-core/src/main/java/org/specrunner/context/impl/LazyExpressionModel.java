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
package org.specrunner.context.impl;

import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * A model with a object source. This model is evaluated only on runtime.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            The return type.
 */
public class LazyExpressionModel<T> implements IModel<T> {

    /**
     * The source of expression.
     */
    protected Object source;

    /**
     * Creates a lazy model from a source.
     * 
     * @param source
     *            The expression source.
     */
    public LazyExpressionModel(Object source) {
        this.source = source;
    }

    /**
     * Gets the source.
     * 
     * @return The source.
     */
    public Object getSource() {
        return source;
    }

    /**
     * Sets the source.
     * 
     * @param source
     *            The source.
     */
    public void setSource(Object source) {
        this.source = source;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject(IContext context) throws ContextException {
        try {
            return (T) UtilEvaluator.evaluate(String.valueOf(source), context);
        } catch (PluginException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new ContextException(e);
        }
    }
}