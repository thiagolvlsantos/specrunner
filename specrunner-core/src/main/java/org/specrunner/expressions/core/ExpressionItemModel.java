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
package org.specrunner.expressions.core;

import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.expressions.ExpressionException;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.expressions.IExpressionItem;
import org.specrunner.expressions.InvalidValueException;
import org.specrunner.util.UtilLog;

/**
 * An expression evaluation item.
 * 
 * @author Thiago Santos
 * 
 */
public class ExpressionItemModel implements IExpressionItem {

    /**
     * Thread safe instance.
     */
    protected static ThreadLocal<IExpressionItem> instance = new ThreadLocal<IExpressionItem>() {
        @Override
        protected IExpressionItem initialValue() {
            return new ExpressionItemModel();
        };
    };

    /**
     * Singleton method.
     * 
     * @return An expression item.
     */
    public static IExpressionItem get() {
        return instance.get();
    }

    @Override
    public Object eval(IExpressionFactory factory, String text, IContext context, boolean silent) throws ExpressionException {
        // check predefined models.
        IModel<?> value = factory.getModels().get(text);
        if (value != null) {
            try {
                return value.getObject(context);
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                if (!silent) {
                    throw new ExpressionException("Unable to evaluate predefined model:" + text, e);
                }
            }
        }
        throw new InvalidValueException("Invalid model '" + text + "'.");
    }
}
