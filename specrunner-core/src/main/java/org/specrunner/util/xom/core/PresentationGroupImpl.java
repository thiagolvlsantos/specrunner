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
package org.specrunner.util.xom.core;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.util.composite.core.CompositeImpl;
import org.specrunner.util.xom.IPresentation;
import org.specrunner.util.xom.IPresentationGroup;

/**
 * A presentation group.
 * 
 * @author Thiago Santos
 * 
 */
public class PresentationGroupImpl extends CompositeImpl<IPresentationGroup, IPresentation> implements IPresentationGroup {

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        for (IPresentation p : getChildren()) {
            sb.append(p.asString());
            sb.append('\n');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public Node asNode() {
        Element e = new Element("span");
        for (IPresentation p : getChildren()) {
            e.appendChild(p.asNode());
        }
        return e;
    }
}
