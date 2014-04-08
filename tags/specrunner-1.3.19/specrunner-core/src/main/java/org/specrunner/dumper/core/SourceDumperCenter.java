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
package org.specrunner.dumper.core;

import java.io.File;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.SRServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.positional.Position;
import org.specrunner.util.UtilLog;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;

/**
 * Dump the central part of the result report (the 'center' element of the
 * framed version).
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperCenter extends AbstractSourceDumperFile {

    /**
     * Number of extra spaces at the end.
     */
    private static final int TAIL = 4;

    @Override
    public void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException {
        set(source, result);
        File output = new File(outputDirectory, outputName);
        model.put("output", String.valueOf(output));
        try {
            Document document = source.getDocument();
            addLinkToFrame(document, result, model);
            saveTo(document, output);
            IOutput out = SRServices.get(IOutputFactory.class).currentOutput();
            out.println("Output (" + SRServices.get(IConcurrentMapping.class).getThread() + "): " + output);
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("DEFAULT   SAVED TO " + output.getAbsolutePath());
            }
        } catch (SourceException e) {
            throw new SourceDumperException(e);
        }
    }

    /**
     * Add a link to body.
     * 
     * @param document
     *            The document.
     * @param result
     *            The result.
     * @param model
     *            The model.
     */
    protected void addLinkToFrame(Document document, IResultSet result, Map<String, Object> model) {
        Nodes nodes = document.query(Position.BODY);
        // if does not have body, add to root element.
        if (nodes.size() == 0) {
            nodes = new Nodes();
            nodes.append(document.getRootElement());
        }
        Element body = (Element) nodes.get(0);
        int size = result.actionTypes().size() + TAIL;
        for (int i = 0; i < size; i++) {
            body.insertChild(new Element("br"), 0);
        }

        Element div = new Element("div");
        div.addAttribute(new Attribute("class", "sr_frame_link_div"));
        body.insertChild(div, 0);

        Element span = new Element("span");
        span.addAttribute(new Attribute("id", "linkFrame"));
        span.addAttribute(new Attribute("class", "sr_frame_link_span"));
        div.appendChild(span);

        Element link = new Element("a");
        span.appendChild(link);
        link.addAttribute(new Attribute("href", detailReport()));
        link.addAttribute(new Attribute("title", "This is a detailed report."));
        link.appendChild("SpecRunner details");
        span.appendChild(" { ");
        Status s = result.getStatus();
        span.appendChild(s.asNode());
        span.appendChild("[" + result.countStatus(s) + "]");
        span.appendChild(" in " + model.get("time") + " ms }: ");
        span.appendChild(new Element("br"));
        span.appendChild(" on " + model.get("date") + " ");
        span.appendChild(new Element("br"));
        span.appendChild(result.asNode());
    }
}
