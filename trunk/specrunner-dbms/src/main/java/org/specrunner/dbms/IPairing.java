package org.specrunner.dbms;

import java.util.Comparator;

public interface IPairing<S, T> {

    Iterable<Pair<S, T>> pair(S parentOld, Iterable<T> old, S parentCurrent, Iterable<T> current, Comparator<T> comparator);
}