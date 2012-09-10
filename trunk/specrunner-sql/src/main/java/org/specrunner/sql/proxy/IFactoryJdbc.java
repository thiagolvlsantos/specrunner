package org.specrunner.sql.proxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Factory of JDBC items.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFactoryJdbc {

    /**
     * Creates a connection.
     * 
     * @param con
     *            A connection.
     * @return A wrapped version.
     */
    Connection newConnection(Connection con);

    /**
     * Creates a statement.
     * 
     * @param stmt
     *            A statement
     * @return A wrapped version.
     */
    Statement newStatement(Statement stmt);

    /**
     * Creates a prepared stament.
     * 
     * @param pstmt
     *            A prepared statement.
     * @return A wrapped version.
     */
    PreparedStatement newPreparedStatement(PreparedStatement pstmt);

    /**
     * Creates a callable statement.
     * 
     * @param cstmt
     *            A callable statement.
     * @return A wrapped version.
     */
    CallableStatement newCallableStatement(CallableStatement cstmt);

    /**
     * Creates a result set.
     * 
     * @param rs
     *            A result set.
     * @return A wrapped version.
     */
    ResultSet newResultSet(ResultSet rs);
}