package org.specrunner.dbms.listeners;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;

import schemacrawler.schema.Table;

public interface ITableListener {

    IPart process(Pair<Table> pair);
}