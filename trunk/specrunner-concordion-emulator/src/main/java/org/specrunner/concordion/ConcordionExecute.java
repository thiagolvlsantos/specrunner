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
 * 'execute' replacer.
 * 
 * @author Thiago Santos.
 * 
 */
public class ConcordionExecute extends ConcordionProcessor {

    /**
     * Default constructor.
     */
    public ConcordionExecute() {
        super("execute");
    }

    @Override
    protected void process(INamespaceInfo info, Document document, Nodes ns) {
        for (int i = 0; i < ns.size(); i++) {
            Element e = (Element) ns.get(i);
            Attribute att = e.getAttribute(getTag(), getUri());
            String value = att.getValue();
            // assignments
            if (value.contains("=")) {
                String[] split = value.split("=");
                e.addAttribute(new Attribute("name", cleanVar(split[0])));
                value = split[1];

            }
            String css = "execute";
            if ("table".equals(e.getLocalName())) {
                css = "executeRows";
            } else {
                Nodes sets = lookup(document, getPrefix(), "set");
                if (sets.size() > 0) {
                    css = "executeLatter";
                }
            }
            e.addAttribute(new Attribute("class", css));
            e.addAttribute(new Attribute("value", "$THIS." + cleanVar(value)));
            e.removeAttribute(att);
        }
    }
}