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
package org.specrunner.sql.meta;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.comparators.IComparator;
import org.specrunner.context.IContext;
import org.specrunner.converters.IConverter;
import org.specrunner.converters.core.ConverterEnumValue;
import org.specrunner.formatters.IFormatter;
import org.specrunner.plugins.PluginException;
import org.specrunner.readers.IReader;
import org.specrunner.util.UtilLog;
import org.specrunner.util.expression.IAbstraction;
import org.specrunner.util.reset.IResetableExtended;

/**
 * Column meta model.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class Column implements IReplicable<Column>, IMergeable<Column>, IResetableExtended {

    /**
     * Column table.
     */
    private Table parent;
    /**
     * Column alias.
     */
    private String alias;
    /**
     * Column name.
     */
    private String name;
    /**
     * Column table name, if required.
     */
    private String table;
    /**
     * Column key indicator.
     */
    private boolean key;
    /**
     * Column flag as a sequence, the attribute value is the sequence name.
     */
    private String sequence;
    /**
     * Indicates a date column.
     */
    private boolean date;
    /**
     * Column default reader.
     */
    private static final IReader READER_DEFAULT = SRServices.getReaderManager().getDefault();
    /**
     * Column reader.
     */
    private IReader reader = READER_DEFAULT;
    /**
     * Column default converter.
     */
    private static final IConverter CONVERTER_DEFAULT = SRServices.getConverterManager().getDefault();
    /**
     * Column converter.
     */
    private IConverter converter = CONVERTER_DEFAULT;
    /**
     * Default list of arguments.
     */
    private static final List<String> ARGUMENTS_DEFAULT = new LinkedList<String>();
    /**
     * List of arguments.
     */
    private List<String> arguments = new LinkedList<String>(ARGUMENTS_DEFAULT);
    /**
     * Column default comparator.
     */
    private static final IComparator COMPARATOR_DEFAULT = SRServices.getComparatorManager().getDefault();
    /**
     * Column comparator.
     */
    private IComparator comparator = COMPARATOR_DEFAULT;
    /**
     * Column default formatter.
     */
    private static final IFormatter FORMATTER_DEFAULT = null;
    /**
     * Column formatter.
     */
    private IFormatter formatter = FORMATTER_DEFAULT;
    /**
     * Default list of arguments.
     */
    private static final List<String> FORMATTER_ARGUMENTS_DEFAULT = new LinkedList<String>();
    /**
     * List of arguments.
     */
    private List<String> formatterArguments = new LinkedList<String>(FORMATTER_ARGUMENTS_DEFAULT);
    /**
     * Column default expression.
     */
    private IAbstraction defaultExpression;
    /**
     * Column default value.
     */
    private Object defaultValue;
    /**
     * Column reference indicator.
     */
    private boolean reference;
    /**
     * Virtual column indicator.
     */
    private boolean virtual;
    /**
     * Column to lookup for virtual column type.
     */
    private String pointer;

    /**
     * Get the column table.
     * 
     * @return The table.
     */
    public Table getParent() {
        return parent;
    }

    /**
     * Set the table.
     * 
     * @param parent
     *            The table.
     * @return The column itself.
     */
    public Column setParent(Table parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Get the table alias.
     * 
     * @return The table name, if exists, null otherwise.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Set the table alias.
     * 
     * @param alias
     *            The alias.
     * @return The table itself.
     */
    public Column setAlias(String alias) {
        this.alias = alias == null ? null : UtilNames.normalize(alias);
        return this;
    }

    /**
     * Get the table name.
     * 
     * @return The table.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the table name.
     * 
     * @param name
     *            The name.
     * @return The column itself.
     */
    public Column setName(String name) {
        this.name = name == null ? null : name.toUpperCase();
        return this;
    }

    /**
     * Get column table reference.
     * 
     * @return Column table name if filled, otherwise, null.
     */
    public String getTable() {
        return table;
    }

    /**
     * Set table property.
     * 
     * @param table
     *            A table name.
     * @return The column itself.
     */
    public Column setTable(String table) {
        this.table = table == null ? null : UtilNames.normalize(table);
        return this;
    }

    /**
     * If column has table attribute returns it, otherwise return alias.
     * 
     * @return Table if exist prior to alias.
     */
    public String getTableOrAlias() {
        return getTable() != null ? getTable() : getAlias();
    }

    /**
     * Get the key information.
     * 
     * @return true, if the column is a key, false, otherwise.
     */
    public boolean isKey() {
        return key;
    }

    /**
     * Set the key flag.
     * 
     * @param key
     *            The value.
     * @return The column itself.
     */
    public Column setKey(boolean key) {
        this.key = key;
        return this;
    }

    /**
     * Get sequence name, is column is a sequence.
     * 
     * @return Sequence name.
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Set sequence name.
     * 
     * @param sequence
     *            The sequence.
     * @return The column itself.
     */
    public Column setSequence(String sequence) {
        this.sequence = sequence;
        return this;
    }

    /**
     * Check if the column is a sequence.
     * 
     * @return true, if column is a sequence, false, otherwise.
     */
    public boolean isSequence() {
        return sequence != null;
    }

    /**
     * Get if the column is a date.
     * 
     * @return true, if date column, false, otherwise.
     */
    public boolean isDate() {
        return date;
    }

    /**
     * Set date flag.
     * 
     * @param date
     *            The flag.
     * @return The column itself.
     */
    public Column setDate(boolean date) {
        this.date = date;
        return this;
    }

    /**
     * Get the column reader.
     * 
     * @return The reader.
     */
    public IReader getReader() {
        return reader;
    }

    /**
     * Set the column reader.
     * 
     * @param reader
     *            The reader.
     * @return The column itself.
     */
    public Column setReader(IReader reader) {
        if (reader != null) {
            this.reader = reader;
        }
        return this;
    }

    /**
     * Get the column converter.
     * 
     * @return The converter.
     */
    public IConverter getConverter() {
        return converter;
    }

    /**
     * Set the column converter.
     * 
     * @param converter
     *            The converter.
     * @return The column itself.
     */
    public Column setConverter(IConverter converter) {
        if (converter != null) {
            this.converter = converter;
        }
        return this;
    }

    /**
     * Get the list of arguments.
     * 
     * @return Arguments.
     */
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * Set the list of arguments.
     * 
     * @param arguments
     *            List of arguments.
     * @return The column itself.
     */
    public Column setArguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    /**
     * Check if a column is a enumeration.
     * 
     * @return true, if converter is enumeration based, false, otherwise.
     */
    public boolean isEnum() {
        return getConverter() instanceof ConverterEnumValue;
    }

    /**
     * Get the column comparator.
     * 
     * @return The comparator.
     */
    public IComparator getComparator() {
        return comparator;
    }

    /**
     * Set the column comparator.
     * 
     * @param comparator
     *            The comparator.
     * @return The column itself.
     */
    public Column setComparator(IComparator comparator) {
        if (comparator != null) {
            this.comparator = comparator;
        }
        return this;
    }

    /**
     * Get formatter.
     * 
     * @return The column formatter.
     */
    public IFormatter getFormatter() {
        return formatter;
    }

    /**
     * The column formatter.
     * 
     * @param formatter
     *            A formatter.
     * @return The column itself.
     */
    public Column setFormatter(IFormatter formatter) {
        if (formatter != null) {
            this.formatter = formatter;
        }
        return this;
    }

    /**
     * Get formatter arguments.
     * 
     * @return The arguments.
     */
    public List<String> getFormatterArguments() {
        return formatterArguments;
    }

    /**
     * Set column formatter.
     * 
     * @param formatterArguments
     *            Formatter arguments.
     * @return Formatter.
     */
    public Column setFormatterArguments(List<String> formatterArguments) {
        this.formatterArguments = formatterArguments;
        return this;
    }

    /**
     * Get the column default value expression.
     * 
     * @return The default value.
     */
    public IAbstraction getDefaultExpression() {
        return defaultExpression;
    }

    /**
     * Set the column default value expression.
     * 
     * @param defaultExpression
     *            The expression.
     * @return The column itself.
     */
    public Column setDefaultExpression(IAbstraction defaultExpression) {
        this.defaultExpression = defaultExpression;
        return this;
    }

    /**
     * Get the column default value. Default values are expected to be non null.
     * 
     * @return The default value.
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Set the column default value.
     * 
     * @param defaultValue
     *            The value.
     * @return The column itself.
     */
    public Column setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    /**
     * Check if a given column values can be used as a reference for foreign
     * references.
     * 
     * @return true, if reference column, false, otherwise.
     */
    public boolean isReference() {
        return reference;
    }

    /**
     * The reference flag.
     * 
     * @param reference
     *            The flag.
     * @return The column itself.
     */
    public Column setReference(boolean reference) {
        this.reference = reference;
        return this;
    }

    /**
     * Check if a given column must be resolved using virtual keys produced on
     * demand by <code>getGenerateKeys()</code>. When this flag is on, in the
     * specification there must be a previous table with naming matching this
     * column name and the value must match that column reference field.
     * 
     * @return The virtual flag.
     */
    public boolean isVirtual() {
        return virtual;
    }

    /**
     * Set virtual flag.
     * 
     * @param virtual
     *            The virtual flag.
     * @return The column itself.
     */
    public Column setVirtual(boolean virtual) {
        this.virtual = virtual;
        return this;
    }

    /**
     * Get the column pointer to a column with the type of this virtual
     * register.
     * 
     * @return The column to lookup for virtual key table alias.
     */
    public String getPointer() {
        return pointer;
    }

    /**
     * Set the pointer reference.
     * 
     * @param pointer
     *            A pointer.
     * @return The column itself.
     */
    public Column setPointer(String pointer) {
        this.pointer = pointer == null ? null : UtilNames.normalize(pointer);
        return this;
    }

    @Override
    public Column copy() {
        return new Column().setParent(parent).setAlias(alias).setName(name).setTable(table).//
                setKey(key).setSequence(sequence).setDate(date).setReader(reader).setConverter(converter).//
                setArguments(arguments).setComparator(comparator).setFormatter(formatter).setFormatterArguments(formatterArguments).//
                setDefaultExpression(defaultExpression).setDefaultValue(defaultValue).setReference(reference).setVirtual(virtual).//
                setPointer(pointer);
    }

    @Override
    public void merge(Column other) {
        if (other == null) {
            return;
        }
        setAlias(other.alias);
        setName(other.name);
        setKey(other.key);
        setTable(other.table);
        setSequence(other.sequence);
        setDate(other.date);
        setConverter(other.converter);
        setArguments(other.arguments);
        setReader(other.reader);
        setComparator(other.comparator);
        setFormatter(other.formatter);
        setFormatterArguments(other.formatterArguments);
        setDefaultExpression(other.defaultExpression);
        setDefaultValue(other.defaultValue);
        setReference(other.reference);
        setVirtual(other.virtual);
        setPointer(other.pointer);
    }

    @Override
    public void initialize(IContext context) {
        if (defaultExpression != null) {
            try {
                Object tmp = defaultExpression.getObject(context, true);
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("New value for default(" + alias + "," + name + "," + defaultExpression + ") is:" + tmp);
                }
                defaultValue = tmp;
            } catch (PluginException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }
}
