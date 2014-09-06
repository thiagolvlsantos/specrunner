/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.annotator.AnnotatorException;
import org.specrunner.annotator.IAnnotator;
import org.specrunner.context.IBlock;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritable;
import org.specrunner.util.UtilLog;

/**
 * Add/Append title with result information.
 * 
 * @author Thiago Santos
 * 
 */
public class AnnotatorTitle implements IAnnotator {

    @Override
    public void annotate(IResultSet result) throws AnnotatorException {
        for (IResult r : result) {
            IBlock block = r.getBlock();
            if (block.hasNode()) {
                Node node = block.getNode();
                String info = getInfo(r.getWritable());
                addMessage(r, node, info);
                addFailure(r, node, info);
            }
        }
    }

    /**
     * Return information.
     * 
     * @param wrt
     *            The writable.
     * @return A information as string.
     */
    private String getInfo(IWritable wrt) {
        String info = null;
        if (wrt != null && wrt.hasInformation()) {
            info = String.valueOf(wrt.getInformation());
        }
        return info;
    }

    /**
     * Add message.
     * 
     * @param r
     *            The result.
     * @param node
     *            The node.
     * @param info
     *            String information.
     * @throws AnnotatorException
     *             On annotation errors.
     */
    private void addMessage(IResult r, Node node, String info) throws AnnotatorException {
        if (r.hasMessage()) {
            addTitle(node, r.getMessage() + (info != null ? ",INFO:" + info : ""), null);
        }
    }

    /**
     * Add failure information.
     * 
     * @param r
     *            The result set.
     * @param node
     *            The node.
     * @param info
     *            String information.
     * @throws AnnotatorException
     *             On annotation errors.
     */
    private void addFailure(IResult r, Node node, String info) throws AnnotatorException {
        if (!r.hasMessage() && r.hasFailure()) {
            addTitle(node, r.getFailure().getMessage() + (info != null ? ",INFO:" + info : ""), r.getFailure());
        }
    }

    /**
     * Add title to a node based on error.
     * 
     * @param node
     *            The node.
     * @param message
     *            The message.
     * @param error
     *            The error.
     * @throws AnnotatorException
     *             On annotation errors.
     */
    protected void addTitle(Node node, String message, Throwable error) throws AnnotatorException {
        if (node instanceof Element) {
            Element element = (Element) node;
            Attribute old = element.getAttribute("title");
            if (old == null) {
                Attribute title = new Attribute("title", message);
                element.addAttribute(title);
            } else {
                old.setValue(old.getValue() + "; " + message);
            }
        } else {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Error element is not an Element -> " + (node != null ? node.toXML() : "null"), error);
            }
        }
    }
}