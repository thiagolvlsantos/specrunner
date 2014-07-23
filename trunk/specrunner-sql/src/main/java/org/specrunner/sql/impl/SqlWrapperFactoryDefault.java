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
package org.specrunner.sql.impl;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.sql.CommandType;
import org.specrunner.sql.IRegister;
import org.specrunner.sql.ISqlWrapperFactory;
import org.specrunner.sql.SqlWrapper;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Table;
import org.specrunner.sql.meta.Value;

/**
 * Default SQL wrapper factory.
 * 
 * @author Thiago Santos
 */
public class SqlWrapperFactoryDefault implements ISqlWrapperFactory {

    @Override
    public SqlWrapper createInputWrapper(Table table, CommandType command, IRegister register, int expectedCount) {
        switch (command) {
        case INSERT:
            return createInsertWrapper(table, register, expectedCount);
        case UPDATE:
            return createUpdateWrapper(table, register, expectedCount);
        case DELETE:
            return createDeleteWrapper(table, register, expectedCount);
        default:
            return null;
        }
    }

    /**
     * Creates an insert wrapper.
     * 
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param expectedCount
     *            The expected operation count.
     * @return A wrapper.
     */
    protected SqlWrapper createInsertWrapper(Table table, IRegister register, int expectedCount) {
        Map<String, Integer> namesToIndexes = new HashMap<String, Integer>();
        StringBuilder sb = sqlInsert(table, register, namesToIndexes);
        return insertWrapper(sb, namesToIndexes, expectedCount);
    }

    /**
     * Creates the insert SQL.
     * 
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param namesToIndexes
     *            A mapping from names to indexes.
     * @return SQL insert command.
     */
    protected StringBuilder sqlInsert(Table table, IRegister register, Map<String, Integer> namesToIndexes) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + table.getParent().getName() + "." + table.getName());
        StringBuilder sbColumns = new StringBuilder();
        StringBuilder sbValues = new StringBuilder();
        int i = 1;
        for (Value e : register) {
            Column column = e.getColumn();
            sbColumns.append(column.getName() + ",");
            if (column.isSequence()) {
                sbValues.append(e.getValue() + ",");
            } else {
                sbValues.append("?,");
                namesToIndexes.put(column.getName(), i++);
            }
        }
        if (sbColumns.length() > 1) {
            sbColumns.setLength(sbColumns.length() - 1);
        }
        if (sbValues.length() > 1) {
            sbValues.setLength(sbValues.length() - 1);
        }
        sb.append(" (");
        sb.append(sbColumns);
        sb.append(") values (");
        sb.append(sbValues);
        sb.append(")");
        return sb;
    }

    /**
     * Creates an insert wrapper.
     * 
     * @param sb
     *            The command.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            The expected register counter.
     * @return A wrapper.
     */
    protected SqlWrapper insertWrapper(StringBuilder sb, Map<String, Integer> namesToIndexes, int expectedCount) {
        return SqlWrapper.insert(sb.toString(), namesToIndexes, expectedCount);
    }

    /**
     * Creates an update wrapper.
     * 
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param expectedCount
     *            The expected operation count.
     * @return A wrapper.
     */
    protected SqlWrapper createUpdateWrapper(Table table, IRegister register, int expectedCount) {
        Map<String, Integer> namesToIndexes = new HashMap<String, Integer>();
        StringBuilder sb = sqlUpdate(table, register, namesToIndexes);
        return updateWrapper(sb, namesToIndexes, expectedCount);
    }

    /**
     * Creates the update SQL.
     * 
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param namesToIndexes
     *            A mapping from names to indexes.
     * @return SQL update command.
     */
    protected StringBuilder sqlUpdate(Table table, IRegister register, Map<String, Integer> namesToIndexes) {
        StringBuilder sb = new StringBuilder();
        sb.append("update " + table.getParent().getName() + "." + table.getName() + " set ");

        boolean hasKeys = false;
        boolean hasReferences = false;
        for (Value v : register) {
            hasKeys = hasKeys || v.getColumn().isKey();
            hasReferences = hasReferences || v.getColumn().isReference();
        }

        StringBuilder sbColumns = new StringBuilder();
        int i = 1;
        if (hasKeys) {
            for (Value v : register) {
                Column column = v.getColumn();
                if (!column.isKey()) {
                    namesToIndexes.put(column.getName(), i++);
                    sbColumns.append(column.getName() + " = ?,");
                }
            }
        } else if (hasReferences) {
            for (Value v : register) {
                Column column = v.getColumn();
                if (!column.isReference()) {
                    namesToIndexes.put(column.getName(), i++);
                    sbColumns.append(column.getName() + " = ?,");
                }
            }
        } else {
            for (Value v : register) {
                Column column = v.getColumn();
                namesToIndexes.put(column.getName(), i++);
                sbColumns.append(column.getName() + " = ?,");
            }
        }
        if (sbColumns.length() > 1) {
            sbColumns.setLength(sbColumns.length() - 1);
        }
        sb.append(sbColumns);
        sb.append(" where ");

        StringBuilder sbConditions = new StringBuilder();
        String and = " AND ";
        if (hasKeys) {
            for (Value v : register) {
                Column column = v.getColumn();
                if (column.isKey()) {
                    namesToIndexes.put(column.getName(), i++);
                    sbConditions.append(column.getName() + " = ? " + and);
                }
            }
        } else if (hasReferences) {
            for (Value v : register) {
                Column column = v.getColumn();
                if (column.isReference()) {
                    namesToIndexes.put(column.getName(), i++);
                    sbConditions.append(column.getName() + " = ? " + and);
                }
            }
        } else {
            for (Value v : register) {
                Column column = v.getColumn();
                namesToIndexes.put(column.getName(), i++);
                sbConditions.append(column.getName() + " = ? " + and);
            }
        }
        if (sbConditions.length() > (1 + and.length())) {
            sbConditions.setLength(sbConditions.length() - (1 + and.length()));
        }
        sb.append(sbConditions);
        return sb;
    }

    /**
     * Creates an update wrapper.
     * 
     * @param sb
     *            The command.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            The command expected counter.
     * @return A wrapper.
     */
    protected SqlWrapper updateWrapper(StringBuilder sb, Map<String, Integer> namesToIndexes, int expectedCount) {
        return SqlWrapper.update(sb.toString(), namesToIndexes, expectedCount);
    }

    /**
     * Creates a delete wrapper.
     * 
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param expectedCount
     *            The expected operation count.
     * @return A wrapper.
     */
    protected SqlWrapper createDeleteWrapper(Table table, IRegister register, int expectedCount) {
        Map<String, Integer> namesToIndexes = new HashMap<String, Integer>();
        StringBuilder sb = sqlDelete(table, register, namesToIndexes);
        return deleteWrapper(sb, namesToIndexes, expectedCount);
    }

    /**
     * Creates the delete SQL.
     * 
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param namesToIndexes
     *            A mapping from names to indexes.
     * @return SQL delete command.
     */
    protected StringBuilder sqlDelete(Table table, IRegister register, Map<String, Integer> namesToIndexes) {
        StringBuilder sb = new StringBuilder();
        sb.append("delete from " + table.getParent().getName() + "." + table.getName() + " where ");

        boolean hasKeys = false;
        boolean hasReferences = false;
        for (Value v : register) {
            hasKeys = hasKeys || v.getColumn().isKey();
            hasReferences = hasReferences || v.getColumn().isReference();
        }

        StringBuilder sbConditions = new StringBuilder();
        int i = 1;
        String and = " AND ";
        if (hasKeys) {
            for (Value v : register) {
                Column column = v.getColumn();
                if (column.isKey()) {
                    namesToIndexes.put(column.getName(), i++);
                    sbConditions.append(column.getName() + " = ?" + and);
                }
            }
        } else if (hasReferences) {
            for (Value v : register) {
                Column column = v.getColumn();
                if (column.isReference()) {
                    namesToIndexes.put(column.getName(), i++);
                    sbConditions.append(column.getName() + " = ?" + and);
                }
            }
        } else {
            for (Value v : register) {
                Column column = v.getColumn();
                namesToIndexes.put(column.getName(), i++);
                sbConditions.append(column.getName() + " = ?" + and);
            }
        }
        if (sbConditions.length() > and.length()) {
            sbConditions.setLength(sbConditions.length() - and.length());
        }
        sb.append(sbConditions);
        return sb;
    }

    /**
     * Creates a delete wrapper.
     * 
     * @param sb
     *            The command.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            The command expected counter.
     * @return A wrapper.
     */
    protected SqlWrapper deleteWrapper(StringBuilder sb, Map<String, Integer> namesToIndexes, int expectedCount) {
        return SqlWrapper.delete(sb.toString(), namesToIndexes, expectedCount);
    }

    @Override
    public SqlWrapper createOutputWrapper(Table table, CommandType command, IRegister register, int expectedCount) {
        return createSelectWrapper(table, command, register, expectedCount);
    }

    /**
     * Creates an select wrapper based on command type.
     * 
     * @param table
     *            A table.
     * @param command
     *            The command type.
     * @param register
     *            A register.
     * @param expectedCount
     *            The expected operation count.
     * @return A wrapper.
     */
    protected SqlWrapper createSelectWrapper(Table table, CommandType command, IRegister register, int expectedCount) {
        Map<String, Integer> namesToIndexes = new HashMap<String, Integer>();
        StringBuilder sb = sqlSelect(table, register, namesToIndexes);
        return selectWrapper(command, sb, namesToIndexes, expectedCount);
    }

    /**
     * Creates the select SQL.
     * 
     * @param table
     *            A table.
     * @param register
     *            A register.
     * @param namesToIndexes
     *            A mapping from names to indexes.
     * @return SQL select command.
     */
    protected StringBuilder sqlSelect(Table table, IRegister register, Map<String, Integer> namesToIndexes) {
        StringBuilder sbVal = new StringBuilder();
        StringBuilder sbPla = new StringBuilder();
        boolean hasKeys = false;
        boolean hasReferences = false;
        for (Value v : register) {
            hasKeys = hasKeys || v.getColumn().isKey();
            hasReferences = hasReferences || v.getColumn().isReference();
        }
        String and = " AND ";
        int i = 1;
        String name;
        if (hasKeys) {
            for (Value v : register) {
                name = v.getColumn().getName();
                if (!v.getColumn().isKey()) {
                    sbVal.append(name + ",");
                } else {
                    sbPla.append(name + (v.getColumn().isDate() ? " between ? and ?" : " = ?") + (i > 0 ? and : ""));
                    namesToIndexes.put(name, i++);
                    if (v.getColumn().isDate()) {
                        i++;
                    }
                }
            }
        } else if (hasReferences) {
            for (Value v : register) {
                name = v.getColumn().getName();
                if (!v.getColumn().isReference()) {
                    sbVal.append(name + ",");
                } else {
                    sbPla.append(name + (v.getColumn().isDate() ? " between ? and ?" : " = ?") + (i > 0 ? and : ""));
                    namesToIndexes.put(name, i++);
                    if (v.getColumn().isDate()) {
                        i++;
                    }
                }
            }
        } else {
            for (Value v : register) {
                name = v.getColumn().getName();
                sbVal.append(name + ",");
                sbPla.append(name + (v.getColumn().isDate() ? " between ? and ?" : " = ?") + (i > 0 ? and : ""));
                namesToIndexes.put(name, i++);
                if (v.getColumn().isDate()) {
                    i++;
                }
            }
        }
        if (sbVal.length() == 0) {
            // when only keys are provided
            for (Value v : register) {
                sbVal.append(v.getColumn().getName() + ",");
            }
        }
        if (sbVal.length() > 1) {
            sbVal.setLength(sbVal.length() - 1);
        }
        if (sbPla.length() > and.length()) {
            sbPla.setLength(sbPla.length() - and.length());
        }
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append(sbVal);
        sb.append(" from " + table.getParent().getName() + "." + table.getName());
        sb.append(" where ");
        sb.append(sbPla);
        return sb;
    }

    /**
     * Creates a wrapper for select commands.
     * 
     * @param command
     *            Command type.
     * @param sb
     *            SQL string.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            Expected count on result.
     * @return A wrapper.
     */
    protected SqlWrapper selectWrapper(CommandType command, StringBuilder sb, Map<String, Integer> namesToIndexes, int expectedCount) {
        switch (command) {
        case INSERT:
            return insertWrapper(sb, namesToIndexes, expectedCount);
        case UPDATE:
            return updateWrapper(sb, namesToIndexes, expectedCount);
        case DELETE:
            return deleteWrapper(sb, namesToIndexes, expectedCount);
        default:
            return null;
        }
    }
}