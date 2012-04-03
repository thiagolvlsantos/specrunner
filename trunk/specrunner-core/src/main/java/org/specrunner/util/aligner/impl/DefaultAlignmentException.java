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
package org.specrunner.util.aligner.impl;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.util.aligner.AlignmentException;
import org.specrunner.util.aligner.IStringAligner;

/**
 * A alignment exception. Useful exception to be raised on string comparison
 * errors.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class DefaultAlignmentException extends AlignmentException {

    /**
     * Creates a exception with an aligner.
     * 
     * @param aligner
     *            The aligner.
     */
    public DefaultAlignmentException(IStringAligner aligner) {
        super(aligner);
    }

    @Override
    public String asString() {
        return getMessage();
    }

    @Override
    public Node asNode() {
        Element ele = new Element("table");
        ele.addAttribute(new Attribute("class", "alignment"));

        Element tr = new Element("tr");
        ele.appendChild(tr);
        Element td = new Element("td");
        td.addAttribute(new Attribute("class", "expected"));
        Element inner = new Element("span");
        inner.addAttribute(new Attribute("class", "small_expected"));
        inner.appendChild("(expected)");
        td.appendChild(inner);
        td.appendChild(aligner.getExpected());
        tr.appendChild(td);

        tr = new Element("tr");
        ele.appendChild(tr);
        td = new Element("td");
        td.addAttribute(new Attribute("class", "received"));
        inner = new Element("span");
        inner.addAttribute(new Attribute("class", "small_received"));
        inner.appendChild("(received)");
        td.appendChild(inner);
        td.appendChild(aligner.getReceived());
        tr.appendChild(td);

        tr = new Element("tr");
        ele.appendChild(tr);
        td = new Element("td");
        tr.appendChild(td);

        addDetail(td);
        return ele;
    }

    /**
     * Provide detailed information of alignment.
     * 
     * @param td
     *            The target node.
     */
    protected void addDetail(Element td) {
        Element tr;
        Element inner;
        Element input = new Element("input");
        String buttonId = "alg_" + System.currentTimeMillis() + "_" + System.nanoTime();
        input.addAttribute(new Attribute("type", "button"));
        input.addAttribute(new Attribute("id", buttonId));
        input.addAttribute(new Attribute("value", " + "));
        input.addAttribute(new Attribute("class", "collapsable"));
        td.appendChild(input);

        Element table = new Element("table");
        table.addAttribute(new Attribute("class", "comparator"));
        table.addAttribute(new Attribute("id", buttonId + "_ref"));
        td.appendChild(table);
        tr = new Element("tr");
        table.appendChild(tr);

        td = new Element("td");
        for (int i = 0; i < 2; i++) {
            Element span = new Element("span");
            span.addAttribute(new Attribute("class", "expected"));
            inner = new Element("span");
            inner.addAttribute(new Attribute("class", "small_expected"));
            inner.appendChild("(expected)");
            span.appendChild(inner);
            td.appendChild(span);
            td.appendChild(new Element("hr"));
            span = new Element("span");
            span.addAttribute(new Attribute("class", "received"));
            inner = new Element("span");
            inner.addAttribute(new Attribute("class", "small_received"));
            inner.appendChild("(received)");
            span.appendChild(inner);
            td.appendChild(span);
            if (i == 0) {
                td.appendChild(new Element("hr"));
            }
        }
        tr.appendChild(td);

        StringBuilder a = aligner.getExpectedAligned();
        StringBuilder b = aligner.getReceivedAligned();
        StringBuilder aT = new StringBuilder();
        StringBuilder bT = new StringBuilder();
        int i = 0;
        while (i < a.length()) {
            aT.setLength(0);
            bT.setLength(0);
            while (i < a.length() && a.charAt(i) == b.charAt(i)) {
                aT.append(a.charAt(i));
                bT.append(b.charAt(i));
                i++;
            }
            if (aT.length() > 0) {
                Element equals = new Element("td");
                equals.addAttribute(new Attribute("class", "equals"));
                equals.appendChild(aT.toString());
                equals.appendChild(new Element("hr"));
                equals.appendChild(bT.toString());
                equals.appendChild(new Element("hr"));
                equals.appendChild(aT.toString());
                equals.appendChild(new Element("hr"));
                equals.appendChild(bT.toString());
                tr.appendChild(equals);
            }
            aT.setLength(0);
            bT.setLength(0);
            while (i < a.length() && a.charAt(i) != b.charAt(i)) {
                aT.append(a.charAt(i));
                bT.append(b.charAt(i));
                i++;
            }
            if (aT.length() > 0) {
                for (int j = 0; j < aT.length(); j++) {
                    Element notEquals = new Element("td");
                    notEquals.addAttribute(new Attribute("class", "notequals"));
                    notEquals.appendChild(aT.charAt(j) + "");
                    notEquals.appendChild(new Element("hr"));
                    notEquals.appendChild(bT.charAt(j) + "");
                    notEquals.appendChild(new Element("hr"));
                    notEquals.appendChild(aT.charAt(j) + "");
                    if (aT.charAt(j) != aligner.getFillCharacter()) {
                        Element span = new Element("span");
                        span.addAttribute(new Attribute("class", "code"));
                        span.appendChild("" + (int) aT.charAt(j));
                        notEquals.appendChild(span);
                    }
                    notEquals.appendChild(new Element("hr"));
                    notEquals.appendChild(bT.charAt(j) + "");
                    if (bT.charAt(j) != aligner.getFillCharacter()) {
                        Element span = new Element("span");
                        span.addAttribute(new Attribute("class", "code"));
                        span.appendChild("" + (int) bT.charAt(j));
                        notEquals.appendChild(span);
                    }
                    tr.appendChild(notEquals);
                }
            }
        }
    }
}