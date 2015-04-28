/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
import org.specrunner.expressions.ExpressionException;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.expressions.IExpressionItem;

/**
 * An expression evaluation item.
 * 
 * @author Thiago Santos
 * 
 */
public class ExpressionItemVar implements IExpressionItem {

    /**
     * Thread safe instance.
     */
    private static ThreadLocal<IExpressionItem> instance = new ThreadLocal<IExpressionItem>() {
        protected IExpressionItem initialValue() {
            return new ExpressionItemVar();
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
        return new ExpressionVariable(factory, text).evaluate(context, silent);
    }
}