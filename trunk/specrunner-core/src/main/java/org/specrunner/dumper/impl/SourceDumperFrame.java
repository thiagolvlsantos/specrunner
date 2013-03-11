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
package org.specrunner.dumper.impl;

import java.io.File;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.specrunner.dumper.SourceDumperException;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.util.UtilLog;

/**
 * Dumper the report frame.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperFrame extends AbstractSourceDumperFile {

    /**
     * Top frame name.
     */
    public static final String TOP_FRAME = "topFrame";
    /**
     * Center frame name.
     */
    public static final String CENTER_FRAME = "centerFrame";
    /**
     * Right frame name.
     */
    public static final String RIGHT_FRAME = "rightFrame";

    /**
     * Top proportion.
     */
    protected String topProportion = "180px";
    /**
     * Center vertical proportion.
     */
    protected String centerVerticalProportion = "80%";
    /**
     * Center horizontal proportion.
     */
    protected String centerHorizontalProportion = "90%";
    /**
     * Right proportion.
     */
    protected String rightProportion = "10%";

    @Override
    public void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException {
        set(source, result);
        String frameName = getFilePrefix() + "_frame.html";
        File output = new File(outputDirectory, frameName);

        Element html = new Element("html");
        Document doc = new Document(html);

        // HORIZONTAL FRAME
        Element horizontalFrame = new Element("frameset");
        horizontalFrame.addAttribute(new Attribute("rows", topProportion + "," + centerVerticalProportion));
        horizontalFrame.addAttribute(new Attribute("framespacing", "0"));
        horizontalFrame.addAttribute(new Attribute("border", "0"));
        horizontalFrame.addAttribute(new Attribute("frameborder", "0"));

        html.appendChild(horizontalFrame);
        {
            // TOP
            Element frameTop = new Element("frame");
            frameTop.addAttribute(new Attribute("name", "top"));
            frameTop.addAttribute(new Attribute("src", String.valueOf(model.get(TOP_FRAME))));
            frameTop.addAttribute(new Attribute("scrolling", "no"));
            frameTop.addAttribute(new Attribute("noresize", "true"));
            frameTop.addAttribute(new Attribute("target", "conteudo"));

            horizontalFrame.appendChild(frameTop);

            // VERTICAL FRAME
            {
                Element verticalFrame = new Element("frameset");
                verticalFrame.addAttribute(new Attribute("name", "middle"));
                verticalFrame.addAttribute(new Attribute("cols", centerHorizontalProportion + "," + rightProportion));
                horizontalFrame.appendChild(verticalFrame);
                {
                    // CENTER
                    Element frameCenter = new Element("frame");
                    frameCenter.addAttribute(new Attribute("name", "center"));
                    frameCenter.addAttribute(new Attribute("src", "../../" + model.get(CENTER_FRAME)));
                    frameCenter.addAttribute(new Attribute("scrolling", "auto"));
                    verticalFrame.appendChild(frameCenter);

                    // RIGHT
                    Element frameRight = new Element("frame");
                    frameRight.addAttribute(new Attribute("name", "right"));
                    frameRight.addAttribute(new Attribute("src", String.valueOf(model.get(RIGHT_FRAME))));
                    frameRight.addAttribute(new Attribute("scrolling", "auto"));
                    frameRight.addAttribute(new Attribute("frameborder", "1"));
                    verticalFrame.appendChild(frameRight);
                }
            }
        }
        saveTo(doc, output);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("FRAMED    SAVED TO " + output.getAbsolutePath());
        }
    }
}