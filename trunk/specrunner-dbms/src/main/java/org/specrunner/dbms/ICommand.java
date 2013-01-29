package org.specrunner.dbms;

import java.util.List;

public interface ICommand {

    ISchema getSchema();

    CommandType getType();

    List<IValue> getValues();
}