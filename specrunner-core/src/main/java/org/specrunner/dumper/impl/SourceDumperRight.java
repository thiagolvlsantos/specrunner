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
import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.context.IBlock;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.source.ISource;
import org.specrunner.util.xom.UtilNode;

/**
 * Dumps the short report of execution (frame right side).
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperRight extends AbstractSourceDumperFile {

    @Override
    public void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException {
        set(source, result);
        String suffix = "_right.html";
        model.put(SourceDumperFrame.RIGHT_FRAME, outputName.substring(0, outputName.lastIndexOf('.')) + suffix);
        String frameName = getFilePrefix() + suffix;
        File output = new File(outputDirectory, frameName);
        Element html = new Element("html");
        Document doc = new Document(html);
        html.appendChild(createLabel(result, model));
        saveTo(doc, output);
        appendResources(output);
    }

    /**
     * Creates the label.
     * 
     * @param result
     *            The result.
     * @param model
     *            The model.
     * @return The label element.
     */
    protected Element createLabel(IResultSet result, Map<String, Object> model) {
        Element body = new Element("body");
        String left = "../../" + (String) model.get(SourceDumperFrame.CENTER_FRAME);
        if (result.getStatus().isError()) {
            Element exp = new Element("input");
            exp.addAttribute(new Attribute("type", "button"));
            exp.addAttribute(new Attribute("id", "right_exp"));
            exp.addAttribute(new Attribute("value", " + "));
            body.appendChild(exp);

            exp = new Element("input");
            exp.addAttribute(new Attribute("type", "button"));
            exp.addAttribute(new Attribute("id", "right_col"));
            exp.addAttribute(new Attribute("value", " - "));
            body.appendChild(exp);

            Element hr = new Element("hr");
            body.appendChild(hr);
        }

        for (Status s : result.availableStatus()) {
            Element status = new Element("div");
            status.addAttribute(new Attribute("class", s.getCssName() + " sr_status"));
            status.appendChild(s.getName());
            status.appendChild("(" + result.countStatus(s) + ")");
            body.appendChild(status);

            if (s.isError()) {
                Element ul = new Element("ul");
                ul.addAttribute(new Attribute("class", "sr_status_list"));
                for (IResult local : result.filterByStatus(s)) {
                    Element li = new Element("li");
                    li.addAttribute(new Attribute("class", "sr_status_item"));
                    IBlock block = local.getBlock();
                    Node target = block.getNode();
                    Nodes nodes = target.query("descendant::a[@name]");
                    if (nodes.size() > 0) {
                        Node no = UtilNode.getHighest(nodes);
                        Element link = new Element("a");
                        String name = ((Element) no).getAttributeValue("name");
                        link.addAttribute(new Attribute("target", "center"));
                        link.addAttribute(new Attribute("href", left + "#" + name));
                        if (!s.isError()) {
                            link.appendChild(name + "-" + s.getName());
                        } else {
                            String msg = local.getFailure().getMessage();
                            if (msg == null) {
                                msg = "Null message.";
                            }
                            link.addAttribute(new Attribute("title", msg));
                            link.appendChild(name + "-" + msg);
                        }
                        li.appendChild(link);
                        ul.appendChild(li);
                    }
                }
                body.appendChild(ul);
            }
        }
        return body;
    }
}
