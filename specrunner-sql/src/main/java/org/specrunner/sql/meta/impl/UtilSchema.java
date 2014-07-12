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

import org.specrunner.comparators.ComparatorException;
import org.specrunner.context.IContext;
import org.specrunner.converters.ConverterException;
import org.specrunner.plugins.PluginException;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.ISchemaLoaderXML;
import org.specrunner.sql.meta.Table;
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
     * @param context
     *            The test context.
     * @param table
     *            The table where column is expected to be.
     * @param column
     *            The column to fill.
     * @param holder
     *            The node holder.
     * @throws ConverterException
     *             On default value conversion errors.
     * @throws ComparatorException
     *             On comparator lookup errors.
     */
    public static void setupColumn(IContext context, Table table, Column column, INodeHolder holder) throws ConverterException, ComparatorException {
        column.setName(holder.getAttribute(ISchemaLoaderXML.ATTR_NAME, column.getName()));
        column.setAlias(holder.getAttribute(ISchemaLoaderXML.ATTR_ALIAS, column.getAlias() != null ? column.getAlias() : column.getName()));
        column.setTable(holder.getAttribute(ISchemaLoaderXML.ATTR_TABLE, column.getTable()));
        column.setKey(column.isKey() || Boolean.parseBoolean(holder.getAttribute(ISchemaLoaderXML.ATT_KEY, ISchemaLoaderXML.DEFAULT_FALSE)));
        column.setSequence(holder.getAttribute(ISchemaLoaderXML.ATT_SEQUENCE, column.getSequence()));
        column.setDate(column.isDate() || Boolean.parseBoolean(holder.getAttribute(ISchemaLoaderXML.ATT_DATE, ISchemaLoaderXML.DEFAULT_FALSE)));
        column.setConverter(holder.getConverter(column.getConverter()));
        column.setArguments(holder.getArguments(column.getArguments()));
        column.setComparator(holder.getComparator(column.getComparator()));
        if (holder.hasAttribute(ISchemaLoaderXML.ATT_DEFAULT)) {
            holder.setAttributeValue(ISchemaLoaderXML.ATT_DEFAULT);
            try {
                column.setDefaultValue(holder.getObject(context, true));
            } catch (PluginException e) {
                throw new ConverterException(e);
            }
        }
        column.setVirtual(column.isVirtual() || Boolean.parseBoolean(holder.getAttribute(ISchemaLoaderXML.ATT_VIRTUAL, ISchemaLoaderXML.DEFAULT_FALSE)));
        column.setReference(column.isReference() || Boolean.parseBoolean(holder.getAttribute(ISchemaLoaderXML.ATT_REFERENCE, ISchemaLoaderXML.DEFAULT_FALSE)));
        column.setPointer(holder.getAttribute(ISchemaLoaderXML.ATTR_POINTER, column.getPointer()));
    }
}
