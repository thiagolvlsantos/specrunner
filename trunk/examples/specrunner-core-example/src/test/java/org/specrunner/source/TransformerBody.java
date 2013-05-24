package org.specrunner.source;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.transformer.ITransformer;

public class TransformerBody implements ITransformer {

    @Override
    public void initialize() {
    }

    @Override
    public ISource transform(ISource source) throws SourceException {
        Document d = source.getDocument();
        Nodes ns = d.getRootElement().query("//span");
        if (ns.size() > 0 && source.getString().endsWith("source.html")) {
            ((Element) ns.get(0)).addAttribute(new Attribute("style", "background-color:blue"));
        }
        return source;
    }
}