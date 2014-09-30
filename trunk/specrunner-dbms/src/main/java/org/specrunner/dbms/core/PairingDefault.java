package org.specrunner.dbms.core;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.dbms.Action;
import org.specrunner.dbms.IPairing;
import org.specrunner.dbms.Pair;

public class PairingDefault<S, T> implements IPairing<S, T> {

    @Override
    public Iterable<Pair<S, T>> pair(S parentOld, Iterable<T> old, S parentCurrent, Iterable<T> current, Comparator<T> comparator) {
        List<Pair<S, T>> result = new LinkedList<Pair<S, T>>();
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
                result.add(new Pair<S, T>(Action.REMOVE, parentOld, tmpOld, null, null));
                readOld = iterOld.hasNext();
                tmpOld = null;
                readCur = false;
            } else if (order > 0) {
                result.add(new Pair<S, T>(Action.ADD, null, null, parentCurrent, tmpCur));
                readOld = false;
                readCur = iterCur.hasNext();
                tmpCur = null;
            } else {
                result.add(new Pair<S, T>(Action.MAINTAIN, parentOld, tmpOld, parentCurrent, tmpCur));
                readOld = iterOld.hasNext();
                readCur = iterCur.hasNext();
                tmpOld = null;
                tmpCur = null;
            }
        }
        return result;
    }
}