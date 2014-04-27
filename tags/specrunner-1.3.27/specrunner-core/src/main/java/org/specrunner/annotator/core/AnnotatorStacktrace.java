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
package org.specrunner.annotator.core;

import java.io.IOException;
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
import org.specrunner.util.UtilException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.IPresentation;

/**
 * Add stack traces to nodes with errors.
 * 
 * @author Thiago Santos
 * 
 */
public class AnnotatorStacktrace implements IAnnotator {

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
                addStackTrace(block.getNode(), r, stackIndex);
            }
        }
    }

    /**
     * Add a stack trace to a node.
     * 
     * @param node
     *            The node.
     * @param result
     *            The result.
     * @param stackIndex
     *            The indexes map.
     * @throws AnnotatorException
     *             On annotator errors.
     */
    protected void addStackTrace(Node node, IResult result, int stackIndex) throws AnnotatorException {
        ParentNode parent = node instanceof ParentNode ? (ParentNode) node : node.getParent();
        if (parent == null) {
            parent = node.getDocument();
        }
        if (parent instanceof Document) {
            parent = ((Document) parent).getRootElement();
        }

        int index = parent.indexOf(node) + 1;

        Element button = new Element("input");
        button.addAttribute(new Attribute("type", "button"));
        String cssName = result.getStatus().getCssName();
        button.addAttribute(new Attribute("class", cssName + " sr_stackbutton"));
        button.addAttribute(new Attribute("value", " + " + stackIndex));
        button.addAttribute(new Attribute("id", cssName + stackIndex));
        parent.insertChild(button, index++);

        Throwable failure = result.getFailure();
        Element stack = null;
        if (failure instanceof IPresentation) {
            // for special exceptions which implement IPresentation interface,
            // the asNode() method is used to print error
            stack = new Element("span");
            stack.appendChild(((IPresentation) failure).asNode());
        } else {
            // otherwise print default stack trace
            stack = new Element("pre");
            try {
                stack.appendChild(UtilException.toString(failure));
            } catch (IOException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new AnnotatorException(e);
            }
        }
        stack.addAttribute(new Attribute("id", cssName + stackIndex + "_stack"));
        stack.addAttribute(new Attribute("class", cssName + " sr_stacktrace"));
        parent.insertChild(stack, index);
    }
}