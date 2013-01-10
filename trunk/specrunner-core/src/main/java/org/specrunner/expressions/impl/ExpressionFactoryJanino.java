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
package org.specrunner.expressions.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.Scanner;
import org.specrunner.context.IContext;
import org.specrunner.expressions.ExpressionException;
import org.specrunner.expressions.IExpression;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Implementation of a expression factory using Janino.
 * 
 * @author Thiago Santos
 * 
 */
public class ExpressionFactoryJanino extends AbstractExpressionFactory {

    @Override
    public IExpression create(Object source, IContext context) throws ExpressionException {
        String expression = String.valueOf(source);

        List<String> args = new LinkedList<String>();
        List<Object> values = new LinkedList<Object>();
        List<Class<?>> types = new LinkedList<Class<?>>();
        arguments(context, expression, args, types, values);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("EXPR>" + expression);
            UtilLog.LOG.debug("ARGS>" + args);
            UtilLog.LOG.debug("VALS>" + values);
            UtilLog.LOG.debug("TYPES>" + types);
        }

        Object r = eval(source, expression, args, types, values);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("JANINO(" + (r != null ? r.getClass().getSimpleName() : "") + ")>" + r);
        }
        return new ExpressionObject(r);
    }

    /**
     * Prepare arguments.
     * 
     * @param context
     *            The context.
     * @param expression
     *            The expression.
     * @param args
     *            The arguments.
     * @param types
     *            The types.
     * @param values
     *            The values.
     */
    protected void arguments(IContext context, String expression, List<String> args, List<Class<?>> types, List<Object> values) {
        Reader rd = null;
        try {
            rd = new StringReader(expression);
            String[] vars = ExpressionEvaluator.guessParameterNames(new Scanner(null, rd));
            for (String str : vars) {
                ExpressionVariable var = new ExpressionVariable(str);
                Object result = var.evaluate(context);
                if (result != null) {
                    args.add(str);
                    values.add(result);
                    types.add(result.getClass());
                } else {
                    // check predefined values
                    Object value = getPredefinedValues().get(str);
                    // if value is itself an expression should be evaluated
                    if (value != null && value instanceof String) {
                        try {
                            value = UtilEvaluator.evaluate((String) value, context);
                        } catch (Exception e) {
                            if (UtilLog.LOG.isTraceEnabled()) {
                                UtilLog.LOG.trace(e.getMessage(), e);
                            }
                        }
                    }
                    if (value != null) {
                        args.add(str);
                        values.add(value);
                        types.add(value.getClass());
                    } else {
                        // check predefined classes
                        Class<?> clazz = getPredefinedClasses().get(str);
                        if (clazz != null) {
                            try {
                                args.add(str);
                                value = clazz.newInstance();
                                values.add(value);
                                types.add(value.getClass());
                            } catch (Exception e) {
                                throw new ExpressionException("Unable to evaluate predefined value:" + str, e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Evaluate the expression.
     * 
     * @param source
     *            The expression source.
     * @param expression
     *            The expression as string.
     * @param args
     *            The arguments.
     * @param types
     *            The argument types.
     * @param values
     *            The argument values.
     * @return The resulting object.
     */
    protected Object eval(Object source, String expression, List<String> args, List<Class<?>> types, List<Object> values) {
        String[] arrayArgs = new String[args.size()];
        args.toArray(arrayArgs);
        Object[] arrayValues = new Object[values.size()];
        values.toArray(arrayValues);
        Class<?>[] arrayTypes = new Class<?>[types.size()];
        types.toArray(arrayTypes);

        Object r = null;
        try {
            r = numericValue(expression);
        } catch (NumberFormatException ne) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(ne.getMessage(), ne);
                UtilLog.LOG.trace("JANINO(" + source + ")_not a number>" + r);
            }
            try {
                ExpressionEvaluator ee = new ExpressionEvaluator(expression, Object.class, arrayArgs, arrayTypes);
                r = ee.evaluate(arrayValues);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("JANINO(" + source + ")_produced>" + r + "(" + (r != null ? r.getClass().getSimpleName() : "") + ")");
                }
            } catch (Exception e) {
                r = expression;
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace(e.getMessage(), e);
                    UtilLog.LOG.trace("JANINO(" + source + ")_unchanged>" + r);
                }
            }
        }
        return r;
    }

    /**
     * Convert to number.
     * 
     * @param original
     *            The text.
     * @return The corresponding value.
     */
    protected Number numericValue(String original) {
        try {
            return Long.valueOf(original);
        } catch (NumberFormatException e) {
            return Double.valueOf(original);
        }
    }
}