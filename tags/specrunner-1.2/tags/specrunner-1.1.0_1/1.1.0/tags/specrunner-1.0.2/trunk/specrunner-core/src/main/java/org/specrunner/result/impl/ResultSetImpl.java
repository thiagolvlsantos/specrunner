/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.result.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.context.IBlock;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritable;
import org.specrunner.result.Status;

/**
 * Default result set implementation.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ResultSetImpl extends LinkedList<IResult> implements IResultSet {

    @Override
    public Status getStatus() {
        Status result = Status.SUCCESS;
        for (IResult s : this) {
            result = result.max(s.getStatus());
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Status> Iterable<T> availableStatus() {
        List<T> result = new LinkedList<T>();
        for (IResult s : this) {
            if (!result.contains(s.getStatus())) {
                result.add((T) s.getStatus());
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Status> List<T> errorStatus() {
        List<T> result = new LinkedList<T>();
        for (IResult s : this) {
            Status r = s.getStatus();
            if (r.isError() && !result.contains(r)) {
                result.add((T) r);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public <T extends Status> List<IResult> filterByStatus(T... status) {
        return filterByStatus(0, size(), status);
    }

    @Override
    public <T extends Status> List<IResult> filterByStatus(int start, int end, T... status) {
        List<IResult> result = new LinkedList<IResult>();
        Set<Status> valid = new HashSet<Status>();
        for (int i = 0; i < status.length; i++) {
            valid.add(status[i]);
        }
        for (int i = start; i < end; i++) {
            IResult t = get(i);
            if (valid.contains(t.getStatus())) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public <T extends Status> int countStatus(T... status) {
        return countStatus(0, size(), status);
    }

    @Override
    public <T extends Status> int countStatus(int start, int end, T... status) {
        return filterByStatus(start, end, status).size();
    }

    @Override
    public IResult addResult(Status status, IBlock source) {
        return addResult(status, source, null, null, null);
    }

    @Override
    public IResult addResult(Status status, IBlock source, IWritable writable) {
        return addResult(status, source, null, null, writable);
    }

    @Override
    public IResult addResult(Status status, IBlock source, String message) {
        return addResult(status, source, message, null, null);
    }

    @Override
    public IResult addResult(Status status, IBlock source, String message, IWritable writable) {
        return addResult(status, source, message, null, writable);
    }

    @Override
    public IResult addResult(Status status, IBlock source, Throwable failure) {
        return addResult(status, source, null, failure, null);
    }

    @Override
    public IResult addResult(Status status, IBlock source, Throwable failure, IWritable writable) {
        return addResult(status, source, null, failure, writable);
    }

    /**
     * Add a result.
     * 
     * @param status
     *            The status.
     * @param source
     *            The source.
     * @param message
     *            The message.
     * @param failure
     *            The failure.
     * @param writable
     *            The writable resources.
     * @return The result new created.
     */
    protected IResult addResult(Status status, IBlock source, String message, Throwable failure, IWritable writable) {
        IResult result = new ResultImpl(status, source, message, failure, writable);
        if (add(result)) {
            return result;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStatus().getName() + "(total:" + size() + ")");
        sb.append("\n");
        for (Status s : availableStatus()) {
            sb.append("  " + s.getName() + "(" + countStatus(s) + ")\n");
            if (s.isError()) {
                for (IResult i : filterByStatus(s)) {
                    sb.append("    " + i + "\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStatus().getName() + "(total:" + size() + ")->");
        int i = 0;
        for (Status s : availableStatus()) {
            if (i++ > 0) {
                sb.append(',');
            }
            sb.append(s.getName() + "(" + countStatus(s) + ")");
        }
        List<Status> errors = errorStatus();
        if (!errors.isEmpty()) {
            sb.append("\n");
            for (Status status : errors) {
                int index = 0;
                List<IResult> filter = filterByStatus(status);
                sb.append("\t" + status.getName() + " (" + filter.size() + "):\n");
                for (IResult r : filter) {
                    sb.append("\t\t[" + (++index));
                    String msg = r.asString();
                    msg = (msg != null ? msg.replace("\n", "\n\t\t\t") : msg);
                    sb.append("]=" + msg + "\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public Node asNode() {
        Element base = new Element("span");
        Element e = (Element) getStatus().asNode();
        e.appendChild("(total:" + size() + ")");
        base.appendChild(e);
        base.appendChild("->");
        int i = 0;
        for (Status s : availableStatus()) {
            if (i++ > 0) {
                base.appendChild(",");
            }
            Element st = (Element) s.asNode();
            st.appendChild("(" + countStatus(s) + ")");
            base.appendChild(st);
        }
        return base;
    }
}