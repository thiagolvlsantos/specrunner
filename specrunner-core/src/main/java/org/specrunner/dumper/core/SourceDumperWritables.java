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
package org.specrunner.dumper.core;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;
import nu.xom.Text;

import org.specrunner.context.IBlock;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritable;
import org.specrunner.result.ResultException;
import org.specrunner.source.ISource;
import org.specrunner.util.UtilLog;

/**
 * Dumps the writables associated to the result set.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperWritables extends AbstractSourceDumperFile {

    /**
     * Label name.
     */
    public static final String LABEL_FIELD = "name";

    @Override
    public void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException {
        set(source, result);
        File dir = new File(outputFile.getAbsolutePath() + "_res/snapshots");
        if (!dir.exists() && !dir.mkdirs()) {
            throw new SourceDumperException("Could not create snapshot directory '" + dir + "'.");
        }
        try {
            clean(dir);
            int index = 0;
            String prefixToRemove = outputDirectory.toURI().toString();
            for (IResult r : result) {
                addWritable(dir, index, prefixToRemove, r);
                index++;
            }
            if (dir.exists() && UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("WRITABLES SAVED TO " + dir.getAbsolutePath());
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceDumperException(e);
        }
    }

    /**
     * Write elements.
     * 
     * @param dir
     *            The snapshot file.
     * @param index
     *            The index.
     * @param prefixToRemove
     *            The text prefix to remove on relative links.
     * @param r
     *            The result set.
     * @throws ResultException
     *             On result exception.
     */
    protected void addWritable(File dir, int index, String prefixToRemove, IResult r) throws ResultException {
        if (r.hasWritable()) {
            IWritable w = r.getWritable();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new ResultException("Could not create details outputFile directory " + dir + ".");
            }
            try {
                File file = new File(dir, "" + index);
                Map<String, String> references = w.writeTo(file.getAbsolutePath());
                IBlock block = r.getBlock();
                if (block.hasNode()) {
                    addElement(index, prefixToRemove, w, references, block);
                }
            } catch (Exception e) {
                // best effort, do not abort on errors
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Add element parts of writable elements.
     * 
     * @param index
     *            The index.
     * @param prefixToRemove
     *            The prefix to remove in case o relative references.
     * @param w
     *            The writable.
     * @param references
     *            A mapping of references.
     * @param block
     *            The block.
     */
    protected void addElement(int index, String prefixToRemove, IWritable w, Map<String, String> references, IBlock block) {
        Node node = block.getNode();
        ParentNode parent = node instanceof ParentNode ? (ParentNode) node : node.getParent();
        for (Entry<String, String> e : references.entrySet()) {
            parent.appendChild(new Text(" "));
            Element link = new Element("a");
            link.addAttribute(new Attribute("class", "sr_" + e.getKey()));
            link.addAttribute(new Attribute("href", e.getValue().substring(prefixToRemove.length())));
            link.addAttribute(new Attribute("target", e.getKey() + "_" + index));
            String text = (w.hasInformation() ? (String) w.getInformation().get(LABEL_FIELD) : null);
            if (text != null) {
                text = e.getKey() + " " + text;
            } else {
                text = e.getKey();
            }
            link.appendChild(text);
            parent.appendChild(link);
        }
    }
}
