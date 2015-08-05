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
package org.specrunner.util.aligner.core;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SRServices;
import org.specrunner.util.aligner.AlignmentException;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;

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
     * Creates a exception with two strings.
     * 
     * @param message
     *            The message.
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     */
    public DefaultAlignmentException(String message, String expected, String received) {
        super(message, SRServices.get(IStringAlignerFactory.class).align(expected, received));
    }

    /**
     * Creates a exception with two strings.
     * 
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     */
    public DefaultAlignmentException(String expected, String received) {
        super("Strings are different:", SRServices.get(IStringAlignerFactory.class).align(expected, received));
    }

    /**
     * Creates a exception with an aligner.
     * 
     * @param message
     *            The message.
     * @param aligner
     *            The aligner.
     */
    public DefaultAlignmentException(String message, IStringAligner aligner) {
        super(message, aligner);
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
     * @param e
     *            The target node.
     */
    protected void addDetail(Element e) {
        Element td = e;
        Element input = new Element("input");
        String buttonId = "alg_" + System.currentTimeMillis() + "_" + System.nanoTime();
        input.addAttribute(new Attribute("type", "button"));
        input.addAttribute(new Attribute("id", buttonId));
        input.addAttribute(new Attribute("value", " + "));
        input.addAttribute(new Attribute("class", "collapsable"));
        td.appendChild(input);

        addTable(td, buttonId);
    }

    /**
     * Add the error table.
     * 
     * @param td
     *            The table place.
     * @param buttonId
     *            The id for the table.
     */
    private void addTable(Element td, String buttonId) {
        Element tr = new Element("tr");
        Element table = new Element("table");
        table.addAttribute(new Attribute("class", "comparator"));
        table.addAttribute(new Attribute("id", buttonId + "_ref"));
        td.appendChild(table);
        table.appendChild(tr);
        addLabels(tr);
        addComparison(tr);
    }

    /**
     * Add label part.
     * 
     * @param tr
     *            The line.
     */
    private void addLabels(Element tr) {
        Element td;
        Element inner;
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
    }

    /**
     * Add the comparison to a line.
     * 
     * @param tr
     *            The line.
     */
    private void addComparison(Element tr) {
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
                addEquals(tr, aT, bT);
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
                    addNotEquals(j, tr, aT, bT);
                }
            }
        }
    }

    /**
     * Add a common part.
     * 
     * @param tr
     *            The line.
     * @param aT
     *            Expected.
     * @param bT
     *            Received.
     */
    protected void addEquals(Element tr, StringBuilder aT, StringBuilder bT) {
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

    /**
     * Add a diferent part.
     * 
     * @param j
     *            The index.
     * @param tr
     *            The line.
     * @param aT
     *            The expected.
     * @param bT
     *            The received.
     */
    protected void addNotEquals(int j, Element tr, StringBuilder aT, StringBuilder bT) {
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
