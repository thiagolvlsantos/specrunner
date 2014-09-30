package org.specrunner.dbms;

import java.util.List;

public interface IAnalyser<S, T> {

    void add(List<IPairListener<S, T>> listeners);

    void remove(List<IPairListener<S, T>> listeners);

    String analyse(Iterable<Pair<S, T>> data, IAnalyserManager analysers, IPairListenerManager manager);
}