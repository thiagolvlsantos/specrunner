package org.specrunner.sql.input.impl;

import java.util.List;

import nu.xom.Element;

import org.specrunner.sql.input.INode;
import org.specrunner.sql.input.IRow;

public class RowXOM extends NodeXOM implements IRow {

    public RowXOM(Element element) {
        super(element);
    }

    @Override
    public List<INode> cells() {
        return toNodeList("td");
    }
}