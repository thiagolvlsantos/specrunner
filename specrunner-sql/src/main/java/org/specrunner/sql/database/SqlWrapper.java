/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.sql.database;

import java.util.Map;

/**
 * Wrapper of SQL commands.
 * 
 * @author Thiago Santos
 * 
 */
public class SqlWrapper {
    /**
     * Command type.
     */
    private CommandType type;
    /**
     * SQL command.
     */
    private String sql;

    /**
     * A mapping from column names to their corresponding index in wrapped SQL.
     */
    private Map<String, Integer> namesToIndexes;

    /**
     * Expected count of command.
     */
    private int expectedCount;

    /**
     * Wrapper for insert.
     */
    private static final ThreadLocal<SqlWrapper> INSERT = new ThreadLocal<SqlWrapper>() {
        protected SqlWrapper initialValue() {
            return new SqlWrapper(CommandType.INSERT);
        };
    };
    /**
     * Wrapper for update.
     */
    private static final ThreadLocal<SqlWrapper> UPDATE = new ThreadLocal<SqlWrapper>() {
        protected SqlWrapper initialValue() {
            return new SqlWrapper(CommandType.UPDATE);
        };
    };
    /**
     * Wrapper for delete.
     */
    private static final ThreadLocal<SqlWrapper> DELETE = new ThreadLocal<SqlWrapper>() {
        protected SqlWrapper initialValue() {
            return new SqlWrapper(CommandType.DELETE);
        };
    };

    /**
     * Default constructor.
     * 
     * @param type
     *            The command type.
     */
    protected SqlWrapper(CommandType type) {
        this.type = type;
    }

    /**
     * Get command type.
     * 
     * @return A command type.
     */
    public CommandType getType() {
        return type;
    }

    /**
     * Get the SQL result.
     * 
     * @return The result.
     */
    public String getSql() {
        return sql;
    }

    /**
     * A column name to index in SQL mapping.
     * 
     * @return The mapping.
     */
    public Map<String, Integer> getNamesToIndexes() {
        return namesToIndexes;
    }

    /**
     * get the expected count.
     * 
     * @return The count.
     */
    public int getExpectedCount() {
        return expectedCount;
    }

    /**
     * Set insert.
     * 
     * @param sql
     *            The command.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            The expected count result.
     * @return The wrapper.
     */
    public static SqlWrapper insert(String sql, Map<String, Integer> namesToIndexes, int expectedCount) {
        return prepare(INSERT.get(), sql, namesToIndexes, expectedCount);
    }

    /**
     * Set update.
     * 
     * @param sql
     *            The command.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            The expected result count.
     * @return The wrapper.
     */
    public static SqlWrapper update(String sql, Map<String, Integer> namesToIndexes, int expectedCount) {
        return prepare(UPDATE.get(), sql, namesToIndexes, expectedCount);
    }

    /**
     * Set delete.
     * 
     * @param sql
     *            The command.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            The expected result count.
     * @return The wrapper.
     */
    public static SqlWrapper delete(String sql, Map<String, Integer> namesToIndexes, int expectedCount) {
        return prepare(DELETE.get(), sql, namesToIndexes, expectedCount);
    }

    /**
     * Get a new wrapper.
     * 
     * @param type
     *            A command type.
     * @param sql
     *            The command.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            The expected result count.
     * @return The wrapper.
     */
    public static SqlWrapper newWrapper(CommandType type, String sql, Map<String, Integer> namesToIndexes, int expectedCount) {
        return prepare(new SqlWrapper(type), sql, namesToIndexes, expectedCount);
    }

    /**
     * Prepare any instance.
     * 
     * @param type
     *            The instance.
     * @param sql
     *            The command.
     * @param namesToIndexes
     *            A mapping from column name to placeholder index.
     * @param expectedCount
     *            The expected result count.
     * @return The wrapper.
     */
    protected static SqlWrapper prepare(SqlWrapper type, String sql, Map<String, Integer> namesToIndexes, int expectedCount) {
        type.sql = sql;
        type.namesToIndexes = namesToIndexes;
        type.expectedCount = expectedCount;
        return type;
    }
}
