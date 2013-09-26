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
package org.specrunner.source.impl;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.util.xom.IPresentation;

public enum Type implements IPresentation {

    FEATURE, SCENARIO, SCENARIO_OUTLINE, EXAMPLES, BACKGROUND, FINALLY;

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public String asString() {
        return asNode().toXML();
    }

    @Override
    public Node asNode() {
        Element result = new Element("span");
        result.addAttribute(new Attribute("class", "bdd"));
        result.appendChild(text());
        return result;
    }

    protected String text() {
        switch (this) {
        case FEATURE:
            return ("Feature:");
        case SCENARIO:
            return ("Scenario:");
        case SCENARIO_OUTLINE:
            return ("Scenario Outline:");
        case EXAMPLES:
            return ("Examples:");
        case BACKGROUND:
            return ("Background:");
        case FINALLY:
            return ("Finally:");
        }
        return null;
    }
}
