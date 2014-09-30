package org.specrunner.dbms.core;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.dbms.IAnalyser;
import org.specrunner.dbms.IPairListener;
import org.specrunner.dbms.Pair;

public abstract class AbstractAnalyser<S, T> implements IAnalyser<S, T> {

    private List<IPairListener<S, T>> listeners = new LinkedList<IPairListener<S, T>>();

    @Override
    public void add(List<IPairListener<S, T>> ls) {
        synchronized (listeners) {
            listeners.addAll(ls);
        }
    }

    @Override
    public void remove(List<IPairListener<S, T>> ls) {
        synchronized (listeners) {
            listeners.removeAll(ls);
        }
    }

    protected String fireProcess(Pair<S, T> p) {
        synchronized (listeners) {
            StringBuilder sb = new StringBuilder();
            for (IPairListener<S, T> pl : listeners) {
                sb.append(pl.process(p));
            }
            return sb.toString();
        }
    }
}