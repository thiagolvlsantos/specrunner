package org.specrunner.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Given two result sets provides an enumeration of both. The default
 * enumeration provide both items (expected and received) when the primary keys.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResultEnumerator {

    /**
     * Check if there are more elements.
     * 
     * @return true, if there are more elements.
     * @throws SQLException
     *             On errors.
     */
    boolean next() throws SQLException;

    /**
     * Get the reference result set.
     * 
     * @return null, if the corresponding reference value does not exist, the
     *         reference result set, otherwise.
     */
    ResultSet getExpected();

    /**
     * Get the system result set.
     * 
     * @return null, if the corresponding system value does not exist, the
     *         system result set, otherwise.
     */
    ResultSet getReceived();
}
