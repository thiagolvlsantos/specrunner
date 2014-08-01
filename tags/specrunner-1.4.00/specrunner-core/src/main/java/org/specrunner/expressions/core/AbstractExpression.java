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
import org.specrunner.expressions.IExpression;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.expressions.IExpressionItem;

/**
 * The expression factory.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractExpression implements IExpression {

    /**
     * The default precedence order.
     */
    public static final IExpressionItem[] DEFAULT_PRECEDENCE = { ExpressionItemVar.get(), ExpressionItemValue.get(), ExpressionItemClass.get(), ExpressionItemModel.get() };

    /**
     * The parent factory.
     */
    private IExpressionFactory parent;

    /**
     * The enumeration order.
     */
    private IExpressionItem[] precedence = DEFAULT_PRECEDENCE;

    /**
     * Basic constructor.
     * 
     * @param parent
     *            The parent.
     */
    protected AbstractExpression(IExpressionFactory parent) {
        this.parent = parent;
    }

    @Override
    public IExpressionFactory getParent() {
        return parent;
    }

    @Override
    public IExpressionItem[] getPrecedence() {
        return precedence;
    }

    @Override
    public void setPrecedence(IExpressionItem[] precedence) {
        this.precedence = precedence;
    }

    @Override
    public Object evaluate(IContext context) throws ExpressionException {
        return evaluate(context, true);
    }
}