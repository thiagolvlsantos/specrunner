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
package org.specrunner.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Table;

/**
 * Default implementation of result set enumeration.
 * 
 * @author Thiago Santos
 * 
 */
public class ResultSetEnumerator implements IResultEnumerator {

    /**
     * Table under analysis.
     */
    private Table table;
    /**
     * Indicate to use reference instead of key fields.
     */
    private Boolean virtual;
    /**
     * Expected data.
     */
    private ResultSet rsExpected;
    /**
     * Received data.
     */
    private ResultSet rsReceived;
    /**
     * Consume expected flag.
     */
    private boolean consumeExpected = true;
    /**
     * Consume received flat.
     */
    private boolean consumeReceived = true;
    /**
     * Has item expected flag.
     */
    private boolean hasExpected = false;
    /**
     * Has item received flat.
     */
    private boolean hasReceived = false;
    /**
     * Read expected flag.
     */
    private boolean readExpected = false;
    /**
     * Read received flat.
     */
    private boolean readReceived = false;

    /**
     * Result set enumerator.
     * 
     * @param table
     *            Table under enumeration.
     * @param virtual
     *            Indicator to use virtual values instead of ids.
     * @param expected
     *            Expected data.
     * @param received
     *            Received data.
     */
    public ResultSetEnumerator(Table table, Boolean virtual, ResultSet expected, ResultSet received) {
        this.table = table;
        this.virtual = virtual;
        this.rsExpected = expected;
        this.rsReceived = received;
    }

    @Override
    public boolean next() throws SQLException {
        if (consumeExpected) {
            hasExpected = rsExpected.next();
        }
        if (consumeReceived) {
            hasReceived = rsReceived.next();
        }
        if (hasExpected && hasReceived) {
            int comp = 0;
            Iterator<Column> columns = (virtual != null && virtual ? table.getReferences() : table.getKeys()).iterator();
            while (columns.hasNext() && comp == 0) {
                Column c = columns.next();
                Object tExp = rsExpected.getObject(c.getName());
                Object tRec = rsReceived.getObject(c.getName());
                comp = c.getComparator().compare(tExp, tRec);
            }
            if (comp < 0) {
                readExpected = true;
                readReceived = false;
            } else if (comp > 0) {
                readExpected = false;
                readReceived = true;
            } else {
                readExpected = true;
                readReceived = true;
            }
        } else if (hasExpected && !hasReceived) {
            readExpected = true;
            readReceived = false;
        } else if (!hasExpected && hasReceived) {
            readExpected = false;
            readReceived = true;
        }
        return readExpected || readReceived;
    }

    @Override
    public ResultSet getExpected() {
        ResultSet t = readExpected ? rsExpected : null;
        consumeExpected = readExpected;
        readExpected = false;
        return t;
    }

    @Override
    public ResultSet getReceived() {
        ResultSet t = readReceived ? rsReceived : null;
        consumeReceived = readReceived;
        readReceived = false;
        return t;
    }
}