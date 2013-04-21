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
package org.specrunner.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.sql.meta.Column;

public class ResultSetEnumerator implements IResultEnumerator {

    private ResultSet rsExpected;
    private ResultSet rsReceived;
    private List<Column> keys;
    private boolean readExp = true;
    private ResultSet exp;
    private boolean readRec = true;
    private ResultSet rec;

    public ResultSetEnumerator(ResultSet rsExpected, ResultSet rsReceived, List<Column> keys) {
        this.rsExpected = rsExpected;
        this.rsReceived = rsReceived;
        this.keys = keys;
    }

    @Override
    public boolean next() throws SQLException {
        List<Object> keysExp = null;
        List<Object> keysRec = null;
        if (readExp && rsExpected.next()) {
            keysExp = new LinkedList<Object>();
            for (Column c : keys) {
                keysExp.add(rsExpected.getObject(c.getName()));
            }
        }
        if (readRec && rsReceived.next()) {
            keysRec = new LinkedList<Object>();
            for (Column c : keys) {
                keysRec.add(rsReceived.getObject(c.getName()));
            }
        }
        if (keysExp == null && keysRec == null) {
            readExp = false;
            exp = null;
            readRec = false;
            rec = null;
        } else if (keysExp != null && keysRec != null) {
            int comp = 0;
            Iterator<Column> it = keys.iterator();
            for (int i = 0; comp == 0 && i < keys.size(); i++) {
                comp = it.next().getComparator().compare(keysExp.get(i), keysRec.get(i));
            }
            if (comp < 0) {
                exp = rsExpected;
                readExp = true;
                rec = null;
            } else if (comp > 0) {
                exp = null;
                rec = rsReceived;
                readRec = true;
            } else {
                exp = rsExpected;
                rec = rsReceived;
                readExp = true;
                readRec = true;
            }
        } else if (keysExp == null) {
            readExp = false;
            exp = null;
        } else if (keysRec == null) {
            readRec = false;
            rec = null;
        }
        return exp != null || rec != null;
    }

    @Override
    public ResultSet getExpected() {
        return exp;
    }

    @Override
    public ResultSet getReceived() {
        return rec;
    }
}