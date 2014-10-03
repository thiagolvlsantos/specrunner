package org.specrunner.dbms;


public class Pair<T> {

    private Action type;
    private T old;
    private T current;

    public Pair(Action type, T old, T current) {
        this.type = type;
        this.old = old;
        this.current = current;
    }

    public Action getType() {
        return type;
    }

    public void setType(Action type) {
        this.type = type;
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
}