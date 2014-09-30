package org.specrunner.dbms;

import java.util.List;

public interface IAnalyserManager {

    <S, T> List<IAnalyser<S, T>> get(Class<S> s, Class<T> t);
}