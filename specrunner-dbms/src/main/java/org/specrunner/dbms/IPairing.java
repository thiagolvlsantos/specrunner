package org.specrunner.dbms;

import java.util.Comparator;

public interface IPairing<T> {

    Iterable<Pair<T>> pair(Iterable<T> old, Iterable<T> current, Comparator<T> comparator);
}