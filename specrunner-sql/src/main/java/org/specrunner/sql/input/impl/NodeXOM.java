package org.specrunner.sql.input.impl;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.sql.input.INode;

public class NodeXOM implements INode {
    protected Element element;

    public NodeXOM(Element element) {
        this.element = element;
    }

    @Override
    public String getAttribute(String name) {
        return element.getAttributeValue(name);
    }

    @Override
    public INode setAttribute(String name, String value) {
        element.addAttribute(new Attribute(name, value));
        return this;
    }

    @Override
    public String getText() {
        return element.getValue();
    }

    protected List<INode> toNodeList(String name) {
        List<INode> result = new LinkedList<INode>();
        Nodes es = element.query("descendant::" + name);
        for (int i = 0; i < es.size(); i++) {
            result.add(new NodeXOM((Element) es.get(i)));
        }
        return result;
    }
}