package org.specrunner.transformer.impl;

import nu.xom.Document;
import nu.xom.Nodes;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.transformer.ITransformer;
import org.specrunner.util.UtilNode;

/**
 * Usefull transformer to set all links in specification as included resources
 * in without the need of adding <code>class='include'</code> by hand in each
 * anchor link.
 * 
 * @author Thiago Santos
 * 
 */
public class TransformerHref implements ITransformer {

    @Override
    public ISource transform(ISource source) throws SourceException {
        Document d = source.getDocument();
        Nodes ns = d.getRootElement().query("//a");
        for (int i = 0; i < ns.size(); i++) {
            UtilNode.appendCss(ns.get(i), "include");
        }
        return source;
    }
}