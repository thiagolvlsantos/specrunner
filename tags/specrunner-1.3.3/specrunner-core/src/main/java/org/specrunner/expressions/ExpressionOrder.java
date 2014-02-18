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
package org.specrunner.expressions;

import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.expressions.core.ExpressionVariable;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * The expression evaluation order.
 * 
 * @author Thiago Santos
 * 
 */
public enum ExpressionOrder {

    /**
     * Evaluation of expression variables.
     */
    VAR {
        @Override
        public Object eval(IExpressionFactory factory, String text, IContext context, boolean silent) throws ExpressionException {
            return new ExpressionVariable(factory, text).evaluate(context, silent);
        }
    },
    /**
     * Evaluation of bound values.
     */
    VALUE {
        @Override
        public Object eval(IExpressionFactory factory, String text, IContext context, boolean silent) throws ExpressionException {
            Object value = factory.getValues().get(text);
            // if value is itself an expression should be evaluated
            if (value instanceof String) {
                try {
                    value = UtilEvaluator.evaluate((String) value, context, silent);
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                    if (!silent) {
                        throw new ExpressionException(e);
                    }
                }
            }
            return value;
        }
    },
    /**
     * Evaluation of class values.
     */
    CLASS {
        @Override
        public Object eval(IExpressionFactory factory, String text, IContext context, boolean silent) throws ExpressionException {
            Object value = null;
            // check predefined classes
            Class<?> clazz = factory.getClasses().get(text);
            if (clazz != null) {
                try {
                    value = clazz.newInstance();
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new ExpressionException("Unable to evaluate predefined value:" + text, e);
                }
            }
            return value;
        }
    },
    /**
     * Evaluation of model values.
     */
    MODEL {
        @Override
        public Object eval(IExpressionFactory factory, String text, IContext context, boolean silent) throws ExpressionException {
            Object value = null;
            // check predefined models.
            IModel<?> model = factory.getModels().get(text);
            if (model != null) {
                try {
                    value = model.getObject(context);
                } catch (Exception e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new ExpressionException("Unable to evaluate predefined model:" + text, e);
                }
            }
            return value;
        }
    };

    /**
     * Defines a generic evaluation method.
     * 
     * @param factory
     *            A expression factory.
     * @param text
     *            The expression text.
     * @param context
     *            The test context.
     * @param silent
     *            The silent mode.
     * @return The resulting value.
     * @throws ExpressionException
     *             On expression errors.
     */
    public abstract Object eval(IExpressionFactory factory, String text, IContext context, boolean silent) throws ExpressionException;
}