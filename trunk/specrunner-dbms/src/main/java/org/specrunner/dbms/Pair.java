package org.specrunner.dbms;

public class Pair<P, T> {

    private Action type;
    private P parentOld;
    private T old;
    private P parentCurrent;
    private T current;

    public Pair(Action type, P parentOld, T old, P parentCurrent, T current) {
        this.type = type;
        this.parentOld = parentOld;
        this.old = old;
        this.parentCurrent = parentCurrent;
        this.current = current;
    }

    public Action getType() {
        return type;
    }

    public void setType(Action type) {
        this.type = type;
    }

    public P getParentOld() {
        return parentOld;
    }

    public void setParentOld(P parentOld) {
        this.parentOld = parentOld;
    }

    public P getParentCurrent() {
        return parentCurrent;
    }

    public void setParentCurrent(P parentCurrent) {
        this.parentCurrent = parentCurrent;
    }

    public T getOld() {
        return old;
    }

    public void setOld(T old) {
        this.old = old;
    }

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    @Override
    public String toString() {
        return (type == Action.MAINTAIN ? "" : type + ",") + (type == Action.REMOVE || type == Action.MAINTAIN ? old : "") + (type == Action.ADD ? current : "");
    }
}