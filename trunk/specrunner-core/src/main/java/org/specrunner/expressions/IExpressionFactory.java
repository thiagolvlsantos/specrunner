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
package org.specrunner.expressions;

import java.util.Map;

import org.specrunner.context.IContext;

/**
 * Creates expression based on information, and hold predefined or class based
 * expressions.
 * 
 * @author Thiago Santos
 * 
 */
public interface IExpressionFactory {

    /**
     * For expression creation the priority for name resolution is:
     * <ul>
     * <li>first - context variables;</li>
     * <li>second - predefined values;</li>
     * <li>third - class generated values.</li>
     * </ul>
     * 
     * @param source
     *            Expression information source.
     * @param context
     *            Runner`s context.
     * @return The corresponding expression.
     * @throws ExpressionException
     *             On expression errors.
     */
    IExpression create(Object source, IContext context) throws ExpressionException;

    /**
     * Remove all predefined values for expressions. Any object can be
     * predefined, for example a SQL connection could be bound to a name
     * "connection" and be used anywhere in test specification.
     */
    void clearPredefinedValues();

    /**
     * Remove a predefined value by name.
     * 
     * @param name
     *            The value name to be removed.
     */
    void removePredefinedValue(String name);

    /**
     * Binds a predefined value to a name.
     * 
     * @param name
     *            The predefined value name.
     * @param value
     *            The predefined value.
     */
    void bindPredefinedValue(String name, Object value);

    /**
     * Set predefined values in block.
     * 
     * @param predefinedValues
     *            The values.
     */
    void setPredefinedValues(Map<String, Object> predefinedValues);

    /**
     * The set of predefined values.
     * 
     * @return A map of name to predefined object instances.
     */
    Map<String, Object> getPredefinedValues();

    /**
     * Clear predefined class values. Class values are those generated by
     * calling default constructor of mapped type. i.e. if java.util.Data is
     * bound to 'd', every expression with 'd' will be automatically replaced by
     * a new instance of date.
     */
    void clearPredefinedClasses();

    /**
     * Remove a predefined class by name.
     * 
     * @param name
     *            The predefined class.
     */
    void removePredefinedClass(String name);

    /**
     * Bind a class to a predefined name.
     * 
     * @param name
     *            The predefined name.
     * @param clazz
     *            The predefined class.
     */
    void bindPredefinedClass(String name, Class<?> clazz);

    /**
     * Set predefined classes in block.
     * 
     * @param predefinedClasses
     *            Predefined classes.
     */
    void setPredefinedClasses(Map<String, Class<?>> predefinedClasses);

    /**
     * The set of predefined classes.
     * 
     * @return A map of name to predefined classes.
     */
    Map<String, Class<?>> getPredefinedClasses();
}