package org.specrunner.application.util.tree;

import java.util.List;

public interface IComposite<P extends IComposite<P, T>, T> {

    boolean isEmpty();

    List<T> getChildren();

    P add(T child);
}
