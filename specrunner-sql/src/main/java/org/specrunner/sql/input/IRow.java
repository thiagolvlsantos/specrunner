package org.specrunner.sql.input;

import java.util.List;

public interface IRow extends INode {

    List<INode> cells();
}