package org.specrunner.sql.report;

import nu.xom.Node;

import org.specrunner.util.xom.IPresentation;

public class ReportException extends Exception implements IPresentation {

    private SchemaReport report;

    public ReportException(SchemaReport report) {
        this.report = report;
    }

    @Override
    public String getMessage() {
        return asString();
    }

    @Override
    public String asString() {
        return report.asString();
    }

    @Override
    public Node asNode() {
        return report.asNode();
    }
}
