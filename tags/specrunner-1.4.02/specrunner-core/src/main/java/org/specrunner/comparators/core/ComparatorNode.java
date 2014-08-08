/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.comparators.core;

import nu.xom.Node;

import org.specrunner.util.UtilString;
import org.specrunner.util.xom.UtilNode;

/**
 * A default node comparator. Compare each node: text-by-text and attribute by
 * attribute.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ComparatorNode extends ComparatorDefault {

    @Override
    public Class<?> getType() {
        return Node.class;
    }

    @Override
    public boolean match(Object expected, Object received) {
        if (expected instanceof Node && received instanceof Node) {
            return toString(expected).equals(toString(received));
        }
        return false;
    }

    @Override
    protected String toString(Object obj) {
        return UtilString.normalize(UtilNode.getChildrenAsString((Node) obj));
    }
}
