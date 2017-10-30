/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
import java.text.NumberFormat;
import java.util.Map;

import org.specrunner.context.IBlock;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.source.ISource;
import org.specrunner.source.resource.IResource;
import org.specrunner.source.resource.positional.core.CSSResource;
import org.specrunner.source.resource.positional.core.JSResource;
import org.specrunner.util.xom.UtilNode;

import nu.xom.Attribute;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

/**
 * Dumps the report details in a separate file.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperDetails extends AbstractSourceDumperFile {

    /**
     * Mb size.
     */
    protected static final int MBYTES = 1024 * 1024;

    @Override
    public void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException {
        set(source, result);
        File output = new File(outputDirectory, detailReport());
        Document doc = new Document(new Element("html"));
        doc.setDocType(new DocType("html"));
        head(source, doc.getRootElement());
        body(result, model, doc.getRootElement());
        saveTo(doc, output);
    }

    /**
     * Get header part.
     * 
     * @param source
     *            Source.
     * @param html
     *            Root element.
     */
    protected void head(ISource source, Element html) {
        Element header = new Element("head");
        html.appendChild(header);
        for (IResource ir : source.getManager()) {
            if (ir instanceof CSSResource || ir instanceof JSResource) {
                for (Node node : ir.getNodes()) {
                    if (node != null) {
                        header.appendChild(replaced(outputName + "_res/", node.copy()));
                    }
                }
            }
        }
    }

    /**
     * Replace reference names to current directory.
     * 
     * @param old
     *            Old value.
     * @param node
     *            Base node.
     * @return Node adjusted.
     */
    protected Node replaced(String old, Node node) {
        if (node instanceof Element) {
            Element e = (Element) node;
            for (int i = 0; i < e.getAttributeCount(); i++) {
                Attribute att = e.getAttribute(i);
                att.setValue(att.getValue().replace(old, ""));
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            replaced(old, node.getChild(i));
        }
        return node;
    }

    /**
     * Creates the label.
     * 
     * @param result
     *            The result.
     * @param model
     *            The model.
     * @param html
     *            The HTML root element.
     */
    protected void body(IResultSet result, Map<String, Object> model, Element html) {
        Element body = new Element("body");
        html.appendChild(body);
        {
            header(result, model, body);
            body.appendChild(new Element("hr"));
            details(result, body);
            body.appendChild(new Element("hr"));
            footer(result, model, body);
        }
    }

    /**
     * Set document start.
     * 
     * @param result
     *            The result.
     * @param model
     *            The model.
     * @param body
     *            The body tag.
     */
    protected void header(IResultSet result, Map<String, Object> model, Element body) {
        Element table = new Element("table");
        table.addAttribute(new Attribute("class", "sr_table"));
        body.appendChild(table);
        {
            Element tr = new Element("tr");
            table.appendChild(tr);
            {
                Element td = new Element("td");
                td.addAttribute(new Attribute("class", "sr_resume " + result.getStatus().getCssName()));
                tr.appendChild(td);
                {
                    Element legend = new Element("span");
                    legend.addAttribute(new Attribute("class", "sr_legend"));
                    td.appendChild(legend);
                    {
                        Element name = new Element("span");
                        name.addAttribute(new Attribute("class", "sr_name"));
                        legend.appendChild(name);
                        {
                            name.appendChild("SpecRunner");
                        }

                        Element spanTime = new Element("span");
                        spanTime.addAttribute(new Attribute("class", "sr_milis"));
                        legend.appendChild(spanTime);
                        {
                            spanTime.appendChild(" in " + model.get("time") + " ms");
                        }

                        legend.appendChild(new Element("br"));

                        Element spanOn = new Element("span");
                        spanOn.addAttribute(new Attribute("class", "sr_on"));
                        legend.appendChild(spanOn);
                        {
                            spanOn.appendChild(" on " + model.get("date"));
                        }
                    }
                }
            }

            tr = new Element("tr");
            table.appendChild(tr);
            {
                Element td = new Element("td");
                tr.appendChild(td);
                {
                    td.appendChild(result.asNode());
                }
            }
        }
    }

    /**
     * Add report details.
     * 
     * @param result
     *            The result.
     * @param body
     *            The body tag.
     */
    protected void details(IResultSet result, Element body) {
        for (Status s : result.availableStatus()) {
            Element status = new Element("div");
            status.addAttribute(new Attribute("class", s.getCssName() + " sr_status"));
            body.appendChild(status);
            {
                status.appendChild(s.getName());
                status.appendChild("(" + result.countStatus(s) + ")");
            }
            if (s.isError()) {
                Element ul = new Element("ul");
                ul.addAttribute(new Attribute("class", "sr_status_list"));
                body.appendChild(ul);
                {
                    for (IResult local : result.filterByStatus(s)) {
                        IBlock block = local.getBlock();
                        Node target = block.getNode();
                        Nodes nodes = target.query("descendant::a[@name]");
                        if (nodes.size() > 0) {
                            Element li = new Element("li");
                            li.addAttribute(new Attribute("class", "sr_status_item"));
                            {
                                Element link = new Element("a");
                                String name = ((Element) UtilNode.getHighest(nodes)).getAttributeValue("name");
                                link.addAttribute(new Attribute("href", outputName + "#" + name));
                                link.addAttribute(new Attribute("id", name));
                                li.appendChild(link);
                                {
                                    String prefix = name.substring(local.getStatus().getCssName().length());
                                    if (!s.isError()) {
                                        link.appendChild(prefix + "-" + s.getName());
                                    } else {
                                        Throwable failure = local.getFailure();
                                        String msg = null;
                                        if (failure != null) {
                                            msg = failure.getMessage();
                                            if (msg == null) {
                                                msg = "Null message.";
                                            }
                                        } else {
                                            msg = "Failure missing.";
                                        }
                                        link.addAttribute(new Attribute("title", msg));
                                        link.appendChild(prefix + "-" + msg);
                                    }
                                }
                            }
                            ul.appendChild(li);
                        }
                    }
                }
            }
        }
    }

    /**
     * Add report ending.
     * 
     * @param result
     *            The result.
     * @param model
     *            The model.
     * @param body
     *            The body.
     */
    protected void footer(IResultSet result, Map<String, Object> model, Element body) {
        Element table = new Element("table");
        table.addAttribute(new Attribute("class", "sr_table"));
        body.appendChild(table);
        {
            Element tr = new Element("tr");
            table.appendChild(tr);
            {
                Element td = new Element("td");
                tr.appendChild(td);
                {
                    Runtime runtime = Runtime.getRuntime();
                    // before information
                    double freeBefore = (Long) model.get("free") / MBYTES;
                    double totalBefore = (Long) model.get("total") / MBYTES;
                    double maxBefore = (Long) model.get("max") / MBYTES;
                    double freePercBefore = freeBefore / maxBefore;
                    double totalPercBefore = totalBefore / maxBefore;
                    // after line
                    double freeAfter = runtime.freeMemory() / MBYTES;
                    double totalAfter = runtime.totalMemory() / MBYTES;
                    double maxAfter = runtime.maxMemory() / MBYTES;
                    double freePercAfter = freeAfter / maxAfter;
                    double totalPercAfter = totalAfter / maxAfter;
                    // formating
                    NumberFormat nf = NumberFormat.getPercentInstance();
                    nf.setMaximumFractionDigits(2);

                    Element spanProcessors = new Element("span");
                    spanProcessors.addAttribute(new Attribute("class", "sr_processors"));
                    spanProcessors.appendChild("Processors:" + runtime.availableProcessors());
                    td.appendChild(spanProcessors);
                    Element spanMax = new Element("span");
                    spanMax.addAttribute(new Attribute("class", "sr_max_memory"));
                    spanMax.appendChild(" | Max memory:" + maxAfter + " Mb.");
                    td.appendChild(spanMax);

                    Element tableMem = new Element("table");
                    tableMem.addAttribute(new Attribute("class", "sr_memory"));
                    td.appendChild(tableMem);
                    {
                        // trMem1
                        {
                            Element trMem1 = new Element("tr");
                            tableMem.appendChild(trMem1);
                            Element thMem1 = new Element("th");
                            thMem1.appendChild("Memory");
                            trMem1.appendChild(thMem1);

                            thMem1 = new Element("th");
                            thMem1.appendChild("Before(Mb)");
                            trMem1.appendChild(thMem1);

                            thMem1 = new Element("th");
                            thMem1.appendChild("After(Mb)");
                            trMem1.appendChild(thMem1);

                            thMem1 = new Element("th");
                            thMem1.appendChild("Diff(Mb)");
                            trMem1.appendChild(thMem1);
                        }
                        // trMem2
                        String patternMemory = "%.2f (%s)";
                        {
                            Element trMem2 = new Element("tr");
                            tableMem.appendChild(trMem2);
                            Element tdMem2 = new Element("td");
                            tdMem2.appendChild("Free");
                            trMem2.appendChild(tdMem2);

                            tdMem2 = new Element("td");
                            tdMem2.appendChild(String.format(patternMemory, freeBefore, nf.format(freePercBefore)));
                            trMem2.appendChild(tdMem2);

                            tdMem2 = new Element("td");
                            tdMem2.appendChild(String.format(patternMemory, freeAfter, nf.format(freePercAfter)));
                            trMem2.appendChild(tdMem2);

                            tdMem2 = new Element("td");
                            tdMem2.appendChild(String.format("%.2f", freeAfter - freeBefore));
                            trMem2.appendChild(tdMem2);
                        }
                        // trMem3
                        {
                            Element trMem3 = new Element("tr");
                            tableMem.appendChild(trMem3);
                            Element tdMem3 = new Element("td");
                            tdMem3.appendChild("Total");
                            trMem3.appendChild(tdMem3);
                            tdMem3 = new Element("td");
                            tdMem3.appendChild(String.format(patternMemory, totalBefore, nf.format(totalPercBefore)));
                            trMem3.appendChild(tdMem3);
                            tdMem3 = new Element("td");
                            tdMem3.appendChild(String.format(patternMemory, totalAfter, nf.format(totalPercAfter)));
                            trMem3.appendChild(tdMem3);
                            tdMem3 = new Element("td");
                            tdMem3.appendChild(String.format("%.2f", totalAfter - totalBefore));
                            trMem3.appendChild(tdMem3);
                        }
                    }
                }
            }
        }
    }
}
