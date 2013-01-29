package org.specrunner.application.util.tree;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.MappedSuperclass;

import org.specrunner.application.util.Identifiable;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class CompositeImpl<P extends IComposite<P, T>, T> extends Identifiable implements IComposite<P, T>, Serializable {

    protected P parent;
    protected List<T> children = new LinkedList<T>();

    public void setParent(P parent) {
        this.parent = parent;
    }

    public void setEmpty(boolean empty) {
        // nothing
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }

    @SuppressWarnings("unchecked")
    public P add(T child) {
        children.add(child);
        return (P) this;
    }
}