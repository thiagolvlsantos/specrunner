package org.specrunner.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IResultEnumerator {

    public boolean next() throws SQLException;

    public ResultSet getExpected();

    public ResultSet getReceived();
}
