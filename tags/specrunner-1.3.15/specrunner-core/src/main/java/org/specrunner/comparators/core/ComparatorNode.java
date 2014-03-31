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
package org.specrunner.comparators.core;

import nu.xom.Node;

import org.specrunner.comparators.IComparator;
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
public class ComparatorNode implements IComparator {

    @Override
    public Class<?> getType() {
        return Node.class;
    }

    @Override
    public void initialize() {
    }

    @Override
    public boolean match(Object expected, Object received) {
        String strExpected = UtilString.normalize(UtilNode.getChildrenAsString((Node) expected));
        String strReceived = UtilString.normalize(UtilNode.getChildrenAsString((Node) received));
        return strExpected.equals(strReceived);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public int compare(Object o1, Object o2) {
        if (o1 instanceof Comparable<?> && o2 instanceof Comparable<?>) {
            Comparable left = (Comparable) o1;
            Comparable right = (Comparable) o2;
            return left.compareTo(right);
        }
        return 0;
    }
}
