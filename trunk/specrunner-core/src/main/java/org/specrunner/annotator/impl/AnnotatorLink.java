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
package org.specrunner.annotator.impl;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.context.IBlock;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;

/**
 * Add a anchor link (relative) to errors.
 * 
 * @author Thiago Santos
 * 
 */
public class AnnotatorLink implements IAnnotator {

    @Override
    public void annotate(IResultSet result) throws AnnotatorException {
        int stackIndex = 1;
        for (IResult r : result) {
            IBlock block = r.getBlock();
            if (block.hasNode() && r.hasFailure()) {
                addLinkToError(block.getNode(), stackIndex++);
            }
        }
    }

    /**
     * Add a link to a node.
     * 
     * @param node
     *            The node.
     * @param errorIndex
     *            The index number.
     */
    protected void addLinkToError(Node node, int errorIndex) {
        if (node instanceof ParentNode) {
            ParentNode ele = (ParentNode) node;
            if (ele instanceof Document) {
                ele = ((Document) ele).getRootElement();
            }
            Element child = new Element("a");
            child.addAttribute(new Attribute("name", "" + errorIndex));
            ele.insertChild(child, 0);
        }
    }
}