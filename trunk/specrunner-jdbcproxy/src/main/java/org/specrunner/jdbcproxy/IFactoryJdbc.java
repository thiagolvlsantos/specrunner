package org.specrunner.jdbcproxy;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * Factory of JDBC items.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFactoryJdbc {

    /**
     * Creates a driver.
     * 
     * @param prefix
     *            The URL prefix for driver.
     * @return A wrapped version.
     */
    Driver newDriver(String prefix);

    /**
     * Creates a data source.
     * 
     * @param ds
     *            A data source.
     * @return A wrapped version.
     */
    DataSource newDataSource(DataSource ds);

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