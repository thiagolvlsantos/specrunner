package org.specrunner.dbms;

import java.util.List;

public interface IPairListenerManager {

    <S, T> List<IPairListener<S, T>> get(Class<S> s, Class<T> t);
}