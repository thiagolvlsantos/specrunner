package org.specrunner.sql;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.util.xom.IPresentation;

public class ReportException extends Exception implements IPresentation {

    private List<Object> errors = new LinkedList<Object>();

    public void add(Object error) {
        errors.add(error);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    @Override
    public String getMessage() {
        return asString();
    }

    @Override
    public String asString() {
        return "ERRORS:" + errors;
    }

    @Override
    public Node asNode() {
        return new Element("table");
    }
}
