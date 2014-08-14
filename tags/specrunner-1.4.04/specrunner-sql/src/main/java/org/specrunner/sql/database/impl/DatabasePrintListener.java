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
package org.specrunner.sql.database.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.result.IResultSet;
import org.specrunner.sql.database.DatabaseException;
import org.specrunner.sql.database.DatabaseRegisterEvent;
import org.specrunner.sql.database.DatabaseTableEvent;
import org.specrunner.sql.database.IDatabaseListener;
import org.specrunner.util.output.IOutputFactory;

/**
 * A SQL dumper listener.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class DatabasePrintListener implements IDatabaseListener {

    /**
     * Format timestamps.
     */
    protected SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS");
    /**
     * Format dates.
     */
    protected SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd 00:00:00.00000");

    @Override
    public void initialize() {
    }

    @Override
    public void onTableIn(DatabaseTableEvent event) throws DatabaseException {
    }

    @Override
    public void onRegisterIn(DatabaseRegisterEvent event) throws DatabaseException {
        dump(event.getContext(), event.getResult(), event.getWrapper().getSql(), event.getIndexesToValues());
    }

    @Override
    public void onRegisterOut(DatabaseRegisterEvent event) throws DatabaseException {
        dump(event.getContext(), event.getResult(), event.getWrapper().getSql(), event.getIndexesToValues());
    }

    @Override
    public void onTableOut(DatabaseTableEvent event) throws DatabaseException {
    }

    /**
     * Dump SQL.
     * 
     * @param context
     *            The test context.
     * @param result
     *            The test result.
     * @param sql
     *            The SQL.
     * @param arguments
     *            The arguments.
     */
    public void dump(IContext context, IResultSet result, String sql, Map<Integer, Object> arguments) {
        StringBuilder sb = new StringBuilder();
        int counter = 1;
        char c;
        for (int i = 0; i < sql.length(); i++) {
            c = sql.charAt(i);
            if (c == '?') {
                Object obj = arguments.get(counter++);
                textByType(sb, obj);
            } else {
                sb.append(c);
            }
        }
        sb.append(';');
        print(sb);
    }

    /**
     * Dump current value according to its type.
     * 
     * @param sb
     *            The result.
     * @param obj
     *            An object to dump.
     */
    protected void textByType(StringBuilder sb, Object obj) {
        if (obj == null) {
            sb.append("null");
        } else if (obj instanceof Date) {
            printDate(sb, (Date) obj);
        } else if (obj instanceof Number) {
            sb.append(obj);
        } else {
            sb.append("'" + obj + "'");
        }
    }

    /**
     * Print dates as SQL.
     * 
     * @param sb
     *            The result.
     * @param obj
     *            An object to dump.
     */
    protected void printDate(StringBuilder sb, Date obj) {
        if (obj instanceof Timestamp) {
            sb.append("{ts '" + formatTime.format(obj) + "'}");
        } else {
            sb.append("{ts '" + formatDate.format(obj) + "'}");
        }
    }

    /**
     * Print result.
     * 
     * @param sb
     *            A resolved SQL.
     */
    protected void print(StringBuilder sb) {
        SRServices.get(IOutputFactory.class).currentOutput().println(sb.toString());
    }
}