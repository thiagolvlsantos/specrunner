package org.specrunner.sql.database;

import java.sql.Connection;

import org.specrunner.sql.input.ITable;
import org.specrunner.sql.meta.Schema;

public interface IDatabase {

    void perform(Connection con, Schema schema, ITable data);

    void release();
}