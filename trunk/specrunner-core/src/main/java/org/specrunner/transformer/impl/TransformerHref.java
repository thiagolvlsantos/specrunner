/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.transformer.impl;

import nu.xom.Document;
import nu.xom.Nodes;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.transformer.ITransformer;
import org.specrunner.util.xom.UtilNode;

/**
 * Useful transformer to set all links in specification as included resources
 * without the need of adding <code>class='include'</code> by hand in each
 * anchor link.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class TransformerHref implements ITransformer {

    @Override
    public void initialize() {
    }

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