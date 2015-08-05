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
package org.specrunner.tools.dbms.core;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.tools.dbms.Action;
import org.specrunner.tools.dbms.IPairing;
import org.specrunner.tools.dbms.Pair;

/**
 * Default paring algorithm. Use the comparator and consider incomes already
 * sorted.
 * 
 * @author Thiago Santos
 *
 * @param <T>
 *            Object type.
 */
public class PairingDefault<T> implements IPairing<T> {

    @Override
    public Iterable<Pair<T>> pair(Iterable<T> old, Iterable<T> current, Comparator<T> comparator) {
        List<Pair<T>> result = new LinkedList<Pair<T>>();
        Iterator<T> iterOld = old.iterator();
        Iterator<T> iterCur = current.iterator();
        boolean readOld = iterOld.hasNext(), readCur = iterCur.hasNext();
        T tmpOld = null, tmpCur = null;
        while (readOld || readCur) {
            if (readOld) {
                tmpOld = iterOld.next();
            }
            if (readCur) {
                tmpCur = iterCur.next();
            }
            int order = comparator.compare(tmpOld, tmpCur);
            if (order < 0) {
                result.add(new Pair<T>(Action.REMOVE, tmpOld, null));
                readOld = iterOld.hasNext();
                tmpOld = null;
                readCur = false;
            } else if (order > 0) {
                result.add(new Pair<T>(Action.ADD, null, tmpCur));
                readOld = false;
                readCur = iterCur.hasNext();
                tmpCur = null;
            } else {
                result.add(new Pair<T>(Action.MAINTAIN, tmpOld, tmpCur));
                readOld = iterOld.hasNext();
                readCur = iterCur.hasNext();
                tmpOld = null;
                tmpCur = null;
            }
        }
        return result;
    }
}
