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
package org.specrunner.sql.database;

import java.sql.Connection;

import org.specrunner.context.IContext;
import org.specrunner.expressions.EMode;
import org.specrunner.result.IResultSet;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.xom.node.TableAdapter;

/**
 * Table event.
 * 
 * @author Thiago Santos
 */
// CHECKSTYLE:OFF
public class DatabaseTableEvent {

    private IContext context;
    private IResultSet result;
    private TableAdapter adapter;
    private IDatabase database;
    private Connection connection;
    private Table table;
    private EMode mode;

    public DatabaseTableEvent(IContext context, IResultSet result, TableAdapter adapter, IDatabase database, Connection connection, Table table, EMode mode) {
        this.context = context;
        this.result = result;
        this.adapter = adapter;
        this.database = database;
        this.connection = connection;
        this.table = table;
        this.mode = mode;
    }

    public IContext getContext() {
        return context;
    }

    public IResultSet getResult() {
        return result;
    }

    public TableAdapter getAdapter() {
        return adapter;
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

    public EMode getMode() {
        return mode;
    }
}
// CHECKSTYLE:ON
