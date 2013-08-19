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
import java.text.NumberFormat;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.specrunner.dumper.SourceDumperException;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;

/**
 * Dumps the information header report.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperTop extends AbstractSourceDumperFile {

    /**
     * KB size.
     */
    protected static final int KBYTES = 1024;

    @Override
    public void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException {
        set(source, result);
        String suffix = "_top.html";
        model.put(SourceDumperFrame.TOP_FRAME, outputName.substring(0, outputName.lastIndexOf('.')) + suffix);
        String frameName = getFilePrefix() + suffix;
        File output = new File(outputDirectory, frameName);
        Element html = new Element("html");
        Document doc = new Document(html);
        html.appendChild(createLabel(result, model));
        saveTo(doc, output);
        appendResources(output);
    }

    /**
     * Creates a label.
     * 
     * @param result
     *            The result.
     * @param model
     *            The model.
     * @return The label.
     */
    protected Element createLabel(IResultSet result, Map<String, Object> model) {
        Runtime runtime = Runtime.getRuntime();
        // before information
        long freeBefore = (Long) model.get("free") / KBYTES;
        long totalBefore = (Long) model.get("total") / KBYTES;
        long maxBefore = (Long) model.get("max") / KBYTES;
        double freePercBefore = (double) freeBefore / maxBefore;
        double totalPercBefore = (double) totalBefore / maxBefore;
        // after line
        long freeAfter = runtime.freeMemory() / KBYTES;
        long totalAfter = runtime.totalMemory() / KBYTES;
        long maxAfter = runtime.maxMemory() / KBYTES;
        double freePercAfter = (double) freeAfter / maxAfter;
        double totalPercAfter = (double) totalAfter / maxAfter;
        // formating
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(2);
        // result table
        Element table = new Element("table");
        table.addAttribute(new Attribute("class", "sr_table"));
        // tr1
        {
            Element tr1 = new Element("tr");
            table.appendChild(tr1);
            // td1
            {
                Element td1 = new Element("td");
                tr1.appendChild(td1);

                Element legend = new Element("span");
                legend.addAttribute(new Attribute("class", "sr_legend"));

                Element name = new Element("span");
                legend.appendChild(name);

                name.addAttribute(new Attribute("class", "sr_name"));
                name.appendChild("SpecRunner");

                Element spanOn = new Element("span");
                spanOn.addAttribute(new Attribute("class", "sr_on"));
                spanOn.appendChild("at " + model.get("date"));
                legend.appendChild(spanOn);
                td1.appendChild(legend);

                Element td2 = new Element("td");
                td2.addAttribute(new Attribute("class", "sr_time"));
                td2.addAttribute(new Attribute("nowrap", "nowrap"));
                td2.appendChild("Time:" + model.get("time") + " ms");
                tr1.appendChild(td2);
            }
            // td2
            {
                Element td2 = new Element("td");
                td2.addAttribute(new Attribute("rowspan", "2"));
                tr1.appendChild(td2);

                Element spanProcessors = new Element("span");
                spanProcessors.addAttribute(new Attribute("class", "sr_processors"));
                spanProcessors.appendChild("Processors:" + runtime.availableProcessors());
                td2.appendChild(spanProcessors);
                Element spanMax = new Element("span");
                spanMax.addAttribute(new Attribute("class", "sr_max_memory"));
                spanMax.appendChild(" | Max memory:" + maxAfter + " Kb.");
                td2.appendChild(spanMax);

                Element tableMem = new Element("table");
                tableMem.addAttribute(new Attribute("class", "sr_memory"));
                td2.appendChild(tableMem);
                {
                    // trMem1
                    {
                        Element trMem1 = new Element("tr");
                        tableMem.appendChild(trMem1);
                        Element thMem1 = new Element("th");
                        thMem1.appendChild("Memory");
                        trMem1.appendChild(thMem1);

                        thMem1 = new Element("th");
                        thMem1.appendChild("Before(Kb)");
                        trMem1.appendChild(thMem1);

                        thMem1 = new Element("th");
                        thMem1.appendChild("After(Kb)");
                        trMem1.appendChild(thMem1);

                        thMem1 = new Element("th");
                        thMem1.appendChild("Diff(Kb)");
                        trMem1.appendChild(thMem1);
                    }
                    // trMem2
                    String patternMemory = "%d (%s)";
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
                        tdMem2.appendChild(String.format("%d", freeAfter - freeBefore));
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
                        tdMem3.appendChild(String.format("%d", totalAfter - totalBefore));
                        trMem3.appendChild(tdMem3);
                    }
                }
            }
        }
        // tr2
        {
            getTr2(result, table);
        }
        return table;
    }

    /**
     * Fill tr2.
     * 
     * @param result
     *            The result information.
     * @param table
     *            The result table.
     */
    protected void getTr2(IResultSet result, Element table) {
        Element tr3 = new Element("tr");
        table.appendChild(tr3);
        {
            Element td1 = new Element("td");
            td1.addAttribute(new Attribute("class", "sr_resume " + result.getStatus().getCssName()));
            td1.appendChild(result.asNode());
            tr3.appendChild(td1);
        }
        {
            Element td2 = new Element("td");
            td2.addAttribute(new Attribute("nowrap", "nowrap"));
            td2.addAttribute(new Attribute("align", "center"));
            Element exp = new Element("input");
            exp.addAttribute(new Attribute("type", "button"));
            exp.addAttribute(new Attribute("class", "top_exp"));
            exp.addAttribute(new Attribute("value", " + "));
            td2.appendChild(exp);

            exp = new Element("input");
            exp.addAttribute(new Attribute("type", "button"));
            exp.addAttribute(new Attribute("class", "top_col"));
            exp.addAttribute(new Attribute("value", " - "));
            td2.appendChild(exp);
            tr3.appendChild(td2);
        }
    }
}
