package org.specrunner.dbms;

public interface IPairListener<S, T> {

    String process(Pair<S, T> pair);
}