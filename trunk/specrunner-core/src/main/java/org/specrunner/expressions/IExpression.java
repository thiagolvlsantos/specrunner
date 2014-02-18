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

/**
 * An expression.
 * 
 * @author Thiago Santos
 * 
 */
public interface IExpression {

    /**
     * The mother factory.
     * 
     * @return The factory where the expression came from.
     */
    IExpressionFactory getParent();

    /**
     * Feature to set expression evaluation order.
     */
    String FEATURE_PRECEDENCE = IExpressionFactory.class.getName() + ".precedence";

    /**
     * The default precedence order.
     */
    ExpressionOrder[] DEFAULT_PRECEDENCE = { ExpressionOrder.VAR, ExpressionOrder.VALUE, ExpressionOrder.CLASS, ExpressionOrder.MODEL };

    /**
     * Set evaluation precedence order.
     * 
     * @param precedence
     *            The precedence.
     */
    void setPrecedence(ExpressionOrder[] precedence);

    /**
     * Get evaluation precedence order.
     * 
     * @return The current precedence.
     */
    ExpressionOrder[] getPrecedence();

    /**
     * Given a context, evaluates an expression silently.
     * 
     * @param context
     *            A contextual information.
     * @return The result of expression evaluation.
     * @throws ExpressionException
     *             On evaluation errors.
     */
    Object evaluate(IContext context) throws ExpressionException;

    /**
     * Given a context, evaluates the expression.
     * 
     * @param context
     *            A contextual information.
     * @param silent
     *            <code>true</code>, for evaluation without exceptions,
     *            <code>false</code>, otherwise.
     * @return The result of expression evaluation.
     * @throws ExpressionException
     *             On evaluation errors.
     */
    Object evaluate(IContext context, boolean silent) throws ExpressionException;
}