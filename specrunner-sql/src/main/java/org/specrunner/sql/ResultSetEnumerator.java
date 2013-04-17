package org.specrunner.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.sql.meta.Column;

public class ResultSetEnumerator {

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

    public ResultSet expected() {
        return exp;
    }

    public ResultSet received() {
        return rec;
    }
}