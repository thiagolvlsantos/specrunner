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
package org.specrunner.util.expression;

import java.util.List;

import org.specrunner.comparators.ComparatorException;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;
import org.specrunner.formatters.FormatterException;
import org.specrunner.formatters.IFormatter;
import org.specrunner.plugins.PluginException;
import org.specrunner.readers.IReader;
import org.specrunner.readers.ReaderException;

/**
 * Stands for any object which can be evaluate.
 * 
 * @author Thiago Santos
 * 
 */
public interface IAbstraction {

    /**
     * Feature to eval arguments.
     */
    String FEATURE_EVAL_ARGS = IAbstraction.class.getName() + ".evalArgs";
    /**
     * Default is not eval arguments.
     */
    Boolean DEFAULT_EVAL_ARGS = Boolean.FALSE;

    /**
     * Get element value.
     * 
     * @param context
     *            The test context.
     * @return Element value.
     */
    String getValue(IContext context);

    /**
     * Get element value.
     * 
     * @param context
     *            The test context.
     * @param args
     *            Arguments to return value.
     * 
     * @return Element value.
     */
    String getValue(IContext context, Object[] args);

    /**
     * Get reader object.
     * 
     * @return A reader.
     * @throws ReaderException
     *             On reader load error.
     */
    IReader getReader() throws ReaderException;

    /**
     * Get reader object or default if not found.
     * 
     * @param readerDefault
     *            The default reader.
     * @return A reader.
     * @throws ReaderException
     *             On reader load error.
     */
    IReader getReader(IReader readerDefault) throws ReaderException;

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
     * Get formatter attributes.
     * 
     * @return Formatter attributes.
     */
    List<String> getFormatterArguments();

    /**
     * Get formatter arguments.
     * 
     * @param arguments
     *            Default list of arguments.
     * @return A list of arguments.
     */
    List<String> getFormatterArguments(List<String> arguments);

    /**
     * Get the abstraction formatter.
     * 
     * @return A formatter if it exists, null, otherwise.
     * @throws FormatterException
     *             On formatter lookup error.
     */
    IFormatter getFormatter() throws FormatterException;

    /**
     * Get the abstraction formatter.
     * 
     * @param formatterDefault
     *            Default formatter.
     * @return A formatter if it exists, default otherwise.
     * @throws FormatterException
     *             On formatter lookup error.
     */
    IFormatter getFormatter(IFormatter formatterDefault) throws FormatterException;

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
