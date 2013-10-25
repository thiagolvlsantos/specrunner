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
package org.specrunner.sql.meta.impl;

import java.util.List;

import org.specrunner.comparators.ComparatorException;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.ISchemaLoaderXML;
import org.specrunner.util.xom.INodeHolder;

/**
 * Utility class for Schema manipulation.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilSchema {

    /**
     * Default constructor.
     */
    private UtilSchema() {
    }

    /**
     * Setup a column in a conservative way. Only filled attributes are replaced
     * the old ones remain unchanged.
     * 
     * @param column
     *            The column to fill.
     * @param holder
     *            The node holder.
     * @throws ConverterException
     *             On default value conversion errors.
     * @throws ComparatorException
     *             On comparator lookup errors.
     */
    public static void setupColumn(Column column, INodeHolder holder) throws ConverterException, ComparatorException {
        column.setName(holder.getAttribute(ISchemaLoaderXML.ATTR_NAME, column.getName()));
        column.setAlias(holder.getAttribute(ISchemaLoaderXML.ATTR_ALIAS, column.getAlias() != null ? column.getAlias() : column.getName()));
        column.setKey(column.isKey() || Boolean.parseBoolean(holder.getAttribute(ISchemaLoaderXML.ATT_KEY, ISchemaLoaderXML.DEFAULT_FALSE)));
        column.setSequence(holder.getAttribute(ISchemaLoaderXML.ATT_SEQUENCE, column.getSequence()));
        column.setDate(column.isKey() || Boolean.parseBoolean(holder.getAttribute(ISchemaLoaderXML.ATT_DATE, ISchemaLoaderXML.DEFAULT_FALSE)));
        column.setConverter(holder.getConverter(column.getConverter()));
        column.setArguments(holder.getArguments(column.getArguments()));
        column.setComparator(holder.getComparator(column.getComparator()));
        String defaultValue = holder.getAttribute(ISchemaLoaderXML.ATT_DEFAULT);
        IConverter conv = column.getConverter();
        if (conv.accept(defaultValue)) {
            List<String> args = holder.getArguments();
            Object obj;
            try {
                obj = conv.convert(defaultValue, args.isEmpty() ? null : args.toArray());
            } catch (ConverterException e) {
                throw new ConverterException("Convertion error at table: " + column.getTable().getName() + ", column: " + column.getName() + ". Attempt to convert default value '" + defaultValue + "' using a '" + conv + "'.", e);
            }
            column.setDefaultValue(obj);
        } else {
            column.setDefaultValue(conv.convert(defaultValue, null));
        }
        column.setVirtual(column.isVirtual() || Boolean.parseBoolean(holder.getAttribute(ISchemaLoaderXML.ATT_VIRTUAL, ISchemaLoaderXML.DEFAULT_FALSE)));
        column.setReference(column.isReference() || Boolean.parseBoolean(holder.getAttribute(ISchemaLoaderXML.ATT_REFERENCE, ISchemaLoaderXML.DEFAULT_FALSE)));
    }
}
