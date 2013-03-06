package org.specrunner.sql.input;

public interface INode {

    String getAttribute(String name);

    INode setAttribute(String name, String value);

    String getText();
}