/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IActionType;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.IWritable;
import org.specrunner.result.Status;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.source.SourceException;

/**
 * Default result set implementation.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ResultSetImpl extends LinkedList<IResult> implements IResultSet {

    /**
     * List of expected messages.
     */
    private List<String> messages;

    /**
     * Ordered flag.
     */
    private boolean sorted;

    @Override
    public void setMessages(String[] messages) {
        if (messages != null && messages.length > 0) {
            this.messages = new LinkedList<String>();
            for (int i = 0; i < messages.length; i++) {
                this.messages.add(messages[i]);
            }
        }
    }

    @Override
    public String[] getMessages() {
        return messages == null ? null : messages.toArray(new String[messages.size()]);
    }

    @Override
    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    @Override
    public boolean isSorted() {
        return sorted;
    }

    @Override
    public void consolidate(IContext context) {
        if (messages == null) {
            return;
        }
        List<String> received = new LinkedList<String>();
        for (IResult r : this) {
            if (r.getStatus().isError()) {
                received.add(r.getMessage() != null ? r.getMessage() : r.getFailure().getMessage());
            }
        }
        if (received.isEmpty() && messages.isEmpty()) {
            // received equals to expected
            return;
        }
        StringBuilder errors = new StringBuilder();
        errors.append("Expected messages does not match.");
        if (!received.isEmpty()) {
            errors.append("\nReceived messages missing:" + received);
        }
        if (!messages.isEmpty()) {
            errors.append("\nExpected messages missing:" + messages);
        }
        try {
            addResult(Failure.INSTANCE, context.newBlock(context.getSources().getLast().getDocument().getRootElement(), null), new Exception(errors.toString()));
        } catch (SourceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Status getStatus() {
        Status result = Success.INSTANCE;
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
    public List<ActionType> actionTypes() {
        return actionTypes(this);
    }

    @Override
    public List<ActionType> actionTypes(List<IResult> subset) {
        Set<ActionType> set = new TreeSet<ActionType>();
        for (IResult s : subset) {
            ActionType at = s.getActionType();
            if (!set.contains(at)) {
                set.add(at);
            }
        }
        return new LinkedList<ActionType>(set);
    }

    @Override
    public List<IResult> filterByType(ActionType... actionType) {
        return filterByType(this, actionType);
    }

    @Override
    public List<IResult> filterByType(List<IResult> subset, ActionType... actionType) {
        List<IResult> result = new LinkedList<IResult>();
        Set<ActionType> valid = new HashSet<ActionType>();
        for (int i = 0; i < actionType.length; i++) {
            valid.add(actionType[i]);
        }
        for (IResult r : subset) {
            if (valid.contains(r.getActionType())) {
                result.add(r);
            }
        }
        return result;
    }

    @Override
    public int countType(ActionType... status) {
        return countType(this, status);
    }

    @Override
    public int countType(List<IResult> result, ActionType... status) {
        return filterByType(result, status).size();
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
        status = analiseStatus(status, message, failure);
        IResult result = new ResultImpl(status, source, message, failure, writable);
        if (add(result)) {
            return result;
        }
        return null;
    }

    protected Status analiseStatus(Status status, String message, Throwable failure) {
        if (messages != null && status.isError()) {
            System.out.println("AJUSTAR:" + status + ", " + message + ", " + failure);
            if (!sorted) {
                String tmp = getMessage(message, failure);
                if (messages.contains(tmp)) {
                    messages.remove(tmp);
                    status = Success.INSTANCE;
                }
            } else {
                if (!messages.isEmpty() && messages.get(0).equals(message)) {
                    messages.remove(0);
                    status = Success.INSTANCE;
                }
            }
        }
        return status;
    }

    /**
     * Obtain message from error notification.
     * 
     * @param message
     *            The message.
     * @param failure
     *            The failure.
     * @return The message.
     */
    protected String getMessage(String message, Throwable failure) {
        return message != null ? message : (failure != null ? failure.getMessage() : null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName(getStatus()) + "(total:" + size() + details(this) + ")\n");
        List<IResult> filter;
        for (Status s : availableStatus()) {
            filter = filterByStatus(s);
            sb.append("  " + getName(s) + "(" + countStatus(s) + details(filter) + ")\n");
            if (s.isError()) {
                for (IResult i : filter) {
                    sb.append("    " + i + "\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Returns the name of a given status.
     * 
     * @param s
     *            The status.
     * @return The formated name.
     */
    protected String getName(Status s) {
        return s.getName().toUpperCase();
    }

    /**
     * Generate report by type.
     * 
     * @param list
     *            The result list to be analyzed.
     * @return The short string form.
     */
    protected StringBuilder details(List<IResult> list) {
        StringBuilder sb = new StringBuilder(":[");
        int i = 0;
        for (ActionType t : actionTypes()) {
            if (i++ > 0) {
                sb.append('|');
            }
            int size = filterByType(list, t).size();
            sb.append(t.getName() + "=" + size);
        }
        sb.append("]");
        return sb;
    }

    /**
     * The name for a given interface.
     * 
     * @param t
     *            The interface type.
     * @return The string version.
     */
    protected String getName(Class<IActionType> t) {
        return t.getSimpleName().substring(1).toLowerCase();
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName(getStatus()) + "(total:" + size() + details(this) + ")->");
        int i = 0;
        for (Status s : availableStatus()) {
            if (i++ > 0) {
                sb.append(',');
            }
            sb.append(getName(s) + "(" + countStatus(s) + details(filterByStatus(s)) + ")");
        }
        List<Status> errors = errorStatus();
        if (!errors.isEmpty()) {
            sb.append("\n");
            List<IResult> filter;
            for (Status status : errors) {
                int index = 0;
                filter = filterByStatus(status);
                sb.append("\t" + getName(status) + " (" + filter.size() + details(filter) + "):\n");
                for (IResult r : filter) {
                    sb.append("\t\t[" + (++index));
                    String msg = r.asString();
                    if (msg != null) {
                        msg = msg.replace("\n", "\n\t\t\t");
                    }
                    sb.append("]=" + msg + "\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public Node asNode() {
        Element table = new Element("table");
        table.addAttribute(new Attribute("class", "sr_resultset"));
        Element tr = new Element("tr");
        table.appendChild(tr);
        Element td;
        for (Status s : availableStatus()) {
            td = new Element("th");
            tr.appendChild(td);
            td.addAttribute(new Attribute("class", s.getCssName()));
            td.appendChild(s.asNode());
            td.appendChild("(" + countStatus(s) + ")");
        }
        tr = new Element("tr");
        table.appendChild(tr);
        for (Status s : availableStatus()) {
            td = new Element("td");
            tr.appendChild(td);

            Element sub = new Element("table");
            sub.addAttribute(new Attribute("class", "sr_actiontypes"));
            td.appendChild(sub);
            List<IResult> filter = filterByStatus(s);
            List<ActionType> acs = actionTypes(this);
            for (ActionType at : acs) {
                Element subtr = new Element("tr");
                sub.appendChild(subtr);
                Element subtd = new Element("td");
                subtr.appendChild(subtd);
                subtd.appendChild(at.asNode());
                td.addAttribute(new Attribute("class", at.getCssName()));
                subtd = new Element("td");
                subtr.appendChild(subtd);
                subtd.addAttribute(new Attribute("style", "text-align:right;"));
                subtd.appendChild("" + countType(filter, at));
            }
        }
        return table;
    }
}