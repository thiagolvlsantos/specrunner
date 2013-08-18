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
package org.specrunner.util.xom;

import java.util.List;

import nu.xom.Element;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.util.comparer.ComparatorException;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;

/**
 * Stands for any object which hold an element.
 * 
 * @author Thiago Santos
 * 
 */
public interface IElementHolder {

    /**
     * Get the hold element.
     * 
     * @return The element.
     */
    Element getElement();

    /**
     * Set the hold element.
     * 
     * @param element
     *            The element.
     */
    void setElement(Element element);

    /**
     * Check if an given attribute is present.
     * 
     * @param name
     *            The attribute name.
     * @return true, if present, false, otherwise.
     */
    boolean hasAttribute(String name);

    /**
     * Get attribute value by type.
     * 
     * @param name
     *            The name.
     * @return The value, if exists, null, otherwise.
     */
    String getAttribute(String name);

    /**
     * Set attribute value.
     * 
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    void setAttribute(String name, String value);

    /**
     * Remove a attribute.
     * 
     * @param name
     *            The attribute name.
     */
    void removeAttribute(String name);

    /**
     * Get element value.
     * 
     * @return Element value.
     */
    String getValue();

    /**
     * Get the converter.
     * 
     * @return A converter.
     * @throws ConverterException
     *             On converter lookup error.
     */
    IConverter getConverter() throws ConverterException;

    /**
     * Get the converter, if any, otherwise returns the default converter.
     * 
     * @param converterDefault
     *            The default converter.
     * @return A converter.
     * @throws ConverterException
     *             On converter lookup error.
     */
    IConverter getConverter(IConverter converterDefault) throws ConverterException;

    /**
     * Get element arguments in a array.
     * 
     * @return The list of values set for 'arg0', 'arg1',... 'argN'.
     */
    List<String> getArguments();

    /**
     * Get element arguments in a array.
     * 
     * @param arguments
     *            The default arguments in case of not found.
     * 
     * @return The list of values set for 'arg0', 'arg1',... 'argN'.
     */
    List<String> getArguments(List<String> arguments);

    /**
     * Get the comparator of the given element.
     * 
     * @return The element comparator.
     * @throws ComparatorException
     *             O comparator lookup errors.
     */
    IComparator getComparator() throws ComparatorException;

    /**
     * Get the comparator of the given element.
     * 
     * @param comparatorDefault
     *            The default comparator.
     * @return The element comparator.
     * @throws ComparatorException
     *             O comparator lookup errors.
     */
    IComparator getComparator(IComparator comparatorDefault) throws ComparatorException;

    /**
     * Gets the corresponding value object.
     * 
     * @param context
     *            The context.
     * @param silent
     *            Flag to evaluate silently or not.
     * @return The value object.
     * @throws PluginException
     *             On plugin errors.
     */
    Object getObject(IContext context, boolean silent) throws PluginException;

    /**
     * Gets the corresponding value object.
     * 
     * @param context
     *            The context.
     * @param silent
     *            Flag to evaluate silently or not.
     * @param converter
     *            The converter.
     * @param arguments
     *            The arguments.
     * @return The value object.
     * @throws PluginException
     *             On plugin errors.
     */
    Object getObject(IContext context, boolean silent, IConverter converter, List<String> arguments) throws PluginException;
}