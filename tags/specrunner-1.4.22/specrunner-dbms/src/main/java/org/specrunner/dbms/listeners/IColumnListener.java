package org.specrunner.dbms.listeners;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;

import schemacrawler.schema.Column;

public interface IColumnListener {

    IPart process(Pair<Column> pair);
}