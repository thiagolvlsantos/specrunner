package org.specrunner.dbms;

import java.util.List;

public interface ISchema {

    String getName();

    List<IColumn> getFields();
}
