package org.specrunner.sql.input;

import java.util.List;

public interface ITable extends INode {

    INode caption();

    List<INode> headers();

    List<IRow> rows();
}