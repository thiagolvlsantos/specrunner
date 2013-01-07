/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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

import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.context.IBlock;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.util.ExceptionUtil;
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
        int stackIndex = 1;
        for (IResult r : result) {
            IBlock block = r.getBlock();
            if (block.hasNode()) {
                Node node = block.getNode();
                if (r.getFailure() != null) {
                    addStackTrace(node, r, stackIndex++);
                }
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
     *            The start index.
     * @throws AnnotatorException
     *             On annotator errors.
     */
    protected void addStackTrace(Node node, IResult result, int stackIndex) throws AnnotatorException {
        ParentNode parent = node instanceof ParentNode ? (ParentNode) node : node.getParent();
        if (parent == null) {
            parent = node.getDocument();
        }
        int index = parent.indexOf(node) + 1;

        Element button = new Element("input");
        button.addAttribute(new Attribute("type", "button"));
        button.addAttribute(new Attribute("class", "stackbutton"));
        button.addAttribute(new Attribute("value", " + " + stackIndex));
        button.addAttribute(new Attribute("id", "error" + stackIndex));
        parent.insertChild(button, index++);

        Throwable failure = result.getFailure();
        Element stack = null;
        if (failure instanceof IPresentation) {
            // for special exceptions which implement IPresentation interface,
            // the asNode() method is used to print error
            stack = new Element("span");
            stack.appendChild(((IPresentation) failure).asNode());
        } else {
            // otherwise print default strack trace
            stack = new Element("pre");
            try {
                stack.appendChild(ExceptionUtil.toString(failure));
            } catch (IOException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new AnnotatorException(e);
            }
        }
        stack.addAttribute(new Attribute("id", "error" + stackIndex + "_stack"));
        stack.addAttribute(new Attribute("class", "stacktrace"));
        parent.insertChild(stack, index);
    }
}