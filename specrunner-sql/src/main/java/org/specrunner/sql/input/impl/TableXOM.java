package org.specrunner.sql.input.impl;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.sql.input.INode;
import org.specrunner.sql.input.IRow;
import org.specrunner.sql.input.ITable;

public class TableXOM extends NodeXOM implements ITable {

    public TableXOM(Element element) {
        super(element);
    }

    @Override
    public INode caption() {
        return new NodeXOM(element.getFirstChildElement("caption"));
    }

    @Override
    public List<INode> headers() {
        return toNodeList("th");
    }

    @Override
    public List<IRow> rows() {
        List<IRow> result = new LinkedList<IRow>();
        Nodes es = element.query("descendant::tbody/tr");
        for (int i = 0; i < es.size(); i++) {
            result.add(new RowXOM((Element) es.get(i)));
        }
        return result;
    }
}