/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.annotator.core;

import java.util.HashMap;
import java.util.Map;

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
import org.specrunner.result.Status;

/**
 * Add a anchor link (relative) to errors.
 * 
 * @author Thiago Santos
 * 
 */
public class AnnotatorLink implements IAnnotator {

    @Override
    public void annotate(IResultSet result) throws AnnotatorException {
        Map<Status, Integer> indexes = new HashMap<Status, Integer>();
        for (IResult r : result) {
            IBlock block = r.getBlock();
            if (block.hasNode() && r.hasFailure()) {
                Status status = r.getStatus();
                Integer stackIndex = indexes.get(status);
                if (stackIndex == null) {
                    stackIndex = 0;
                    indexes.put(status, stackIndex);
                }
                stackIndex++;
                indexes.put(status, stackIndex);
                addLinkToError(block.getNode(), status, stackIndex);
            }
        }
    }

    /**
     * Add a link to a node.
     * 
     * @param node
     *            The node.
     * @param status
     *            The annotation status.
     * @param statusIndex
     *            The index number.
     */
    protected void addLinkToError(Node node, Status status, int statusIndex) {
        if (node instanceof ParentNode) {
            ParentNode ele = (ParentNode) node;
            if (ele instanceof Document) {
                ele = ((Document) ele).getRootElement();
            }
            Element child = new Element("a");
            child.addAttribute(new Attribute("name", status.getCssName() + statusIndex));
            ele.insertChild(child, 0);
        }
    }
}
