package org.specrunner.sql.transformer;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.transformer.ITransformer;

public abstract class AbstractTransformerSql implements ITransformer {

    @Override
    public void initialize() {
    }

    @Override
    public ISource transform(ISource source) throws SourceException {
        Document d = source.getDocument();
        Nodes tables = d.query("//table");
        for (int i = 0; i < tables.size(); i++) {
            Element table = (Element) tables.get(i);
            table.addAttribute(new Attribute("class", getValue()));
        }
        return source;
    }

    public abstract String getValue();
}