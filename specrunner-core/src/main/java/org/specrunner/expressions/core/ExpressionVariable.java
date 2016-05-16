/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
import org.specrunner.expressions.InvalidValueException;

/**
 * Expression variable. Lookup by variable name in the context using
 * <code>getByName(&lt;name&gt;)</code> method.
 * 
 * @author Thiago Santos
 * 
 */
public class ExpressionVariable extends AbstractExpression {

    /**
     * The variable name.
     */
    protected String name;

    /**
     * Creates an expression to access a variable.
     * 
     * @param parent
     *            The expression factory.
     * @param name
     *            The variable name.
     */
    public ExpressionVariable(IExpressionFactory parent, String name) {
        super(parent);
        setName(name);
    }

    /**
     * Gets the variable name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the variable name.
     * 
     * @param name
     *            The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object evaluate(IContext context, boolean silent) throws ExpressionException {
        if (context.hasName(name)) {
            return context.getByName(name);
        }
        throw new InvalidValueException("Invalid name '" + name + "' for variable.");
    }
}
