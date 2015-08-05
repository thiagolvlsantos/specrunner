/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.expressions.core;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.commons.compiler.IExpressionEvaluator;
import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.expressions.EMode;
import org.specrunner.expressions.ExpressionException;
import org.specrunner.expressions.IExpression;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.expressions.IExpressionItem;
import org.specrunner.expressions.INullEmptyFeature;
import org.specrunner.expressions.INullEmptyHandler;
import org.specrunner.expressions.InvalidValueException;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * A Janino expression.
 * 
 * @author Thiago Santos
 * 
 */
public class ExpressionJanino extends AbstractExpression {

    /**
     * Feature to enable Janino work with JDK expressions (
     * <code>org.codehaus.commons.compiler.jdk.ExpressionEvaluator</code>).
     */
    public static final String FEATURE_JDK_COMPILER = ExpressionJanino.class.getName() + ".jdk";

    /**
     * The expression source.
     */
    protected Object source;

    /**
     * Cache of expressions.
     */
    protected static ICache<ExpressionKey, IExpressionEvaluator> cache = SRServices.get(ICacheFactory.class).newCache(ExpressionJanino.class.getName());

    /**
     * Basic constructor.
     * 
     * @param parent
     *            The parent factory.
     * @param source
     *            The object source.
     */
    public ExpressionJanino(IExpressionFactory parent, Object source) {
        super(parent);
        this.source = source;
    }

    @Override
    public Object evaluate(IContext context, boolean silent) throws ExpressionException {
        String expression = String.valueOf(source);
        INullEmptyHandler nullEmpty = (INullEmptyHandler) SRServices.getFeatureManager().get(INullEmptyFeature.FEATURE_NULL_EMPTY_HANDLER);
        if (nullEmpty == null) {
            nullEmpty = NullEmptyHandlerDefault.get();
        }
        if (nullEmpty.isEmpty(EMode.OUTPUT, expression)) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("EMPTY VALUE(" + expression + ")");
            }
            return "";
        }
        if (nullEmpty.isNull(EMode.OUTPUT, expression)) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("NULL VALUE(" + expression + ")");
            }
            return null;
        }
        List<String> args = new LinkedList<String>();
        List<Object> values = new LinkedList<Object>();
        List<Class<?>> types = new LinkedList<Class<?>>();
        Object r;
        try {
            r = arguments(context, expression, args, types, values, silent);
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace("DIRECT(" + (r != null ? r.getClass().getSimpleName() : "") + ")>" + r);
            }
        } catch (InvalidValueException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("EXPR>" + expression);
                UtilLog.LOG.debug("ARGS>" + args);
                UtilLog.LOG.debug("VALS>" + values);
                UtilLog.LOG.debug("TYPES>" + types);
            }
            r = eval(source, expression, args, types, values, silent);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("JANINO(" + (r != null ? r.getClass().getSimpleName() : "") + ")>" + r);
            }
        }
        return r;
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
     * @param silent
     *            Silent option.
     * @return The resulting object, null, otherwise.
     * @throws ExpressionException
     *             On expression errors.
     */
    protected Object arguments(IContext context, String expression, List<String> args, List<Class<?>> types, List<Object> values, boolean silent) throws ExpressionException {
        if ("".equals(expression)) {
            return "";
        } else if (expression.equals("true")) {
            return Boolean.TRUE;
        } else if (expression.equals("false")) {
            return Boolean.FALSE;
        }
        Reader rd = null;
        try {
            rd = new StringReader(expression);
            String[] vars;
            try {
                vars = org.codehaus.janino.ExpressionEvaluator.guessParameterNames(new org.codehaus.janino.Scanner(null, rd));
            } catch (Exception e) {
                // invalid expressions are values themselves
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace(e.getMessage(), e);
                }
                if (!silent) {
                    throw new PluginException(e);
                }
                return expression;
            }
            for (String str : vars) {
                try {
                    Object value = getValue(context, str, silent);
                    args.add(str);
                    values.add(value);
                    types.add(value == null ? null : value.getClass());
                } catch (InvalidValueException e) {
                    // ignore
                }
                // if the expression is itself a var, it has already been
                // evaluated.
                if (expression.equals(str)) {
                    // if expression is not converted to a value, it wont be in
                    // the future
                    if (values.isEmpty()) {
                        // the value is the expression itself
                        return expression;
                    } else {
                        // otherwise it is first value.
                        return values.get(0);
                    }
                }
            }
            try {
                // try return as value
                return numericValue(expression);
            } catch (NumberFormatException ne) {
                // if is not a value, and the number of actual parameters is
                // different of parameters the expression it will fail.
                if (vars.length != args.size()) {
                    if (silent) {
                        return expression;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
            if (!silent) {
                throw new ExpressionException(e);
            }
        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                    if (!silent) {
                        throw new ExpressionException(e);
                    }
                }
            }
        }
        // if the heuristic fails, the expression has to be evaluated.
        throw new InvalidValueException("Process expression.");
    }

    /**
     * Get the value for a expression.
     * 
     * @param context
     *            The context.
     * @param text
     *            The expression.
     * @param silent
     *            The silent mode.
     * @return An object.
     * @throws ExpressionException
     *             On expression errors.
     */
    protected Object getValue(IContext context, String text, boolean silent) throws ExpressionException {
        // set precedence according to the environment
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(IExpression.FEATURE_PRECEDENCE, this);
        // get current order
        IExpressionItem[] order = getPrecedence();
        for (int i = 0; i < order.length; i++) {
            try {
                return order[i].eval(getParent(), text, context, silent);
            } catch (InvalidValueException e) {
                // ignore
            }
        }
        throw new InvalidValueException("Value not accepted as result.");
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
     * @param silent
     *            Silent mode.
     * @return The resulting object.
     * @throws ExpressionException
     *             On evaluation errors.
     */
    protected Object eval(Object source, String expression, List<String> args, List<Class<?>> types, List<Object> values, boolean silent) throws ExpressionException {
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
                UtilLog.LOG.trace("JANINO(" + source + ")_not a number. Message:" + ne.getMessage());
            }
            try {
                long time = System.currentTimeMillis();
                try {
                    r = tryObject(Object.class, expression, arrayArgs, arrayValues, arrayTypes);
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("tryObject (Object.class...) succeed: " + (System.currentTimeMillis() - time));
                    }
                } catch (Exception e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("tryObject (Object.class...) failed: " + (System.currentTimeMillis() - time));
                    }
                    r = tryObject(void.class, expression, arrayArgs, arrayValues, arrayTypes);
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("JANINO(" + source + ")_produced>" + r + "(" + (r != null ? r.getClass().getSimpleName() : "") + ")");
                }
            } catch (Exception e) {
                if (!silent) {
                    throw new ExpressionException(e);
                }
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
     * Compute the expression assuming return type will be the specified.
     * 
     * @param returnType
     *            The calling result type.
     * @param expression
     *            The expression to be evaluated.
     * @param arrayArgs
     *            The formal parameters.
     * @param arrayValues
     *            The actual parameters.
     * @param arrayTypes
     *            The argument types.
     * @return The expression result.
     * @throws Exception
     *             On processing errors.
     */
    protected Object tryObject(Class<?> returnType, String expression, String[] arrayArgs, Object[] arrayValues, Class<?>[] arrayTypes) throws Exception {
        IExpressionEvaluator ee = null;
        synchronized (cache) {
            ee = cache.get(ExpressionKey.unique(expression, returnType, arrayArgs, arrayTypes));
            if (ee == null) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("NEW EXPRESSION(" + expression + ") before.");
                }
                IFeatureManager fm = SRServices.getFeatureManager();
                Boolean jdk = (Boolean) fm.get(FEATURE_JDK_COMPILER);
                if (jdk != null && jdk) {
                    ee = new org.codehaus.commons.compiler.jdk.ExpressionEvaluator(expression, returnType, arrayArgs, arrayTypes);
                } else {
                    ee = new org.codehaus.janino.ExpressionEvaluator(expression, returnType, arrayArgs, arrayTypes);
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("NEW EXPRESSION(" + expression + ") after new: " + ee);
                }
                cache.put(new ExpressionKey(expression, Object.class, arrayArgs, arrayTypes), ee);
            }
        }
        long time = System.currentTimeMillis();
        Object obj = ee.evaluate(arrayValues);
        if (UtilLog.LOG.isDebugEnabled()) {
            UtilLog.LOG.debug("Time: " + (System.currentTimeMillis() - time));
        }
        return obj;
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
