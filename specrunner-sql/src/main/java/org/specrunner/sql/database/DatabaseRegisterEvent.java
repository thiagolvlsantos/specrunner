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

import java.sql.Connection;
import java.util.Map;

import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;
import org.specrunner.sql.meta.IRegister;
import org.specrunner.sql.meta.Table;

/**
 * Register event.
 * 
 * @author Thiago Santos
 */
// CHECKSTYLE:OFF
public class DatabaseRegisterEvent {
    private IContext context;
    private IResultSet result;
    private IDatabase database;
    private Connection connection;
    private Table table;
    private IRegister register;
    private SqlWrapper wrapper;
    private Map<Integer, Object> indexesToValues;

    public DatabaseRegisterEvent(IContext context, IResultSet result, IDatabase database, Connection connection, Table table, IRegister register, SqlWrapper wrapper, Map<Integer, Object> indexesToValues) {
        this.context = context;
        this.result = result;
        this.database = database;
        this.connection = connection;
        this.table = table;
        this.register = register;
        this.wrapper = wrapper;
        this.indexesToValues = indexesToValues;
    }

    public IContext getContext() {
        return context;
    }

    public IResultSet getResult() {
        return result;
    }

    public IDatabase getDatabase() {
        return database;
    }

    public Connection getConnection() {
        return connection;
    }

    public Table getTable() {
        return table;
    }

    public IRegister getRegister() {
        return register;
    }

    public SqlWrapper getWrapper() {
        return wrapper;
    }

    public Map<Integer, Object> getIndexesToValues() {
        return indexesToValues;
    }
}
// CHECKSTYLE:ON
