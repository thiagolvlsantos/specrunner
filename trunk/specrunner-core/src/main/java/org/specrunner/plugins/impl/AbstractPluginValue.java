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
package org.specrunner.plugins.impl;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.UtilEvaluator;

/**
 * Stand for a plugin with a value attribute, or a value which must be calculate
 * by evaluating the content of the specification.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginValue extends AbstractPluginScoped {
    /**
     * true, of value must be evaluated as expressions, false, otherwise.
     */
    private boolean eval;
    /**
     * Plugin value.
     */
    private Object value;

    /**
     * Says if the content must be evaluated as an expression.
     * 
     * @return true, if evaluate is enable, false, otherwise. Default is false.
     */
    public boolean isEval() {
        return eval;
    }

    /**
     * Sets evaluation.
     * 
     * @param eval
     *            true, to enable evaluation, false, otherwise.
     */
    public void setEval(boolean eval) {
        this.eval = eval;
    }

    /**
     * Get the value attribute.
     * 
     * @return The value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the value.
     * 
     * @param value
     *            A new value.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Gets a value based on a string. If the plugin attribute value is set use
     * the 'value' attribute, otherwise use the given string.
     * 
     * @param str
     *            The string to be used, in case of value attribute not set.
     * @param evaluate
     *            true, if evaluate as expression is required, false, otherwise.
     * @param context
     *            The context.
     * @return The object result of the evaluation.
     * @throws PluginException
     *             On case of evaluation error.
     */
    protected Object getValue(Object str, boolean evaluate, IContext context) throws PluginException {
        Object obj = null;
        Object val = value != null ? value : str;
        if (val instanceof String) {
            if (evaluate) {
                obj = UtilEvaluator.evaluate((String) val, context);
            } else {
                obj = UtilEvaluator.replace((String) val, context);
            }
        } else {
            obj = val;
        }
        return obj;
    }
}