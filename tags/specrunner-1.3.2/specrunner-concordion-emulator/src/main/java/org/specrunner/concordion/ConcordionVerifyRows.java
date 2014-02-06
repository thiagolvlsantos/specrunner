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
package org.specrunner.concordion;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.source.namespace.INamespaceInfo;

/**
 * 'verifyRows' replacer.
 * 
 * @author Thiago Santos.
 * 
 */
public class ConcordionVerifyRows extends ConcordionProcessor {

    /**
     * Default constructor.
     */
    public ConcordionVerifyRows() {
        super("verifyrows");
    }

    @Override
    protected void process(INamespaceInfo info, Document document, Nodes ns) {
        for (int i = 0; i < ns.size(); i++) {
            Element e = (Element) ns.get(i);
            Attribute att = e.getAttribute(getTag(), getUri());
            e.addAttribute(new Attribute("class", "verifyRows"));
            String value = att.getValue();
            String[] split = value.split(":");
            e.addAttribute(new Attribute("name", cleanVar(split[0])));
            value = split[1];
            e.addAttribute(new Attribute("value", "$THIS." + cleanVar(value)));
            e.removeAttribute(att);
        }
    }
}