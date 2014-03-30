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
package org.specrunner.result.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Document;
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
import org.specrunner.source.ISource;
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
     * Record success flag.
     */
    private boolean recordSuccess = true;
    /**
     * List of expected messages.
     */
    private List<String> messages;

    /**
     * Ordered flag.
     */
    private boolean sorted;

    @Override
    public Boolean getRecordSuccess() {
        return recordSuccess;
    }

    @Override
    public void setRecordSuccess(Boolean recordSuccess) {
        this.recordSuccess = recordSuccess;
    }

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
    public void setSorted(Boolean sorted) {
        this.sorted = sorted;
    }

    @Override
    public Boolean getSorted() {
        return sorted;
    }

    @Override
    public void consolidate(IContext context) {
        if (messages == null) {
            return;
        }
        List<IResult> items = new LinkedList<IResult>();
        List<String> received = new LinkedList<String>();
        for (IResult r : this) {
            if (r.getStatus().isError()) {
                items.add(r);
                received.add(getMessage(r.getMessage(), r.getFailure()));
            }
        }
        StringBuilder errors = new StringBuilder();
        List<String> expected = new LinkedList<String>(messages);
        if (sorted) {
            int i = 0;
            int max = Math.min(received.size(), expected.size());
            for (; i < max; i++) {
                if (!expected.get(i).equals(received.get(i))) {
                    errors.append("Expected '" + expected.get(i) + "' does not match received '" + received.get(i) + "'.\n");
                } else {
                    items.get(i).setStatus(Success.INSTANCE);
                }
            }
            if (max < expected.size()) {
                errors.append("Expected messages missing:\n");
                append(errors, expected.subList(max, expected.size()));
                errors.append('\n');
            }
            if (max < received.size()) {
                errors.append("Unexpected messages received:\n");
                append(errors, received.subList(max, received.size()));
                errors.append('\n');
            }
        } else {
            List<String> extraReceived = new LinkedList<String>(received);
            List<String> extraExpected = new LinkedList<String>(expected);
            extraReceived.removeAll(expected);
            extraExpected.removeAll(received);
            if (extraExpected.size() > 0) {
                errors.append("Expected messages missing:\n");
                append(errors, extraExpected);
                errors.append('\n');
            }
            if (extraReceived.size() > 0) {
                errors.append("Unexpected messages received:\n");
                append(errors, extraReceived);
                errors.append('\n');
            }
        }
        if (errors.length() == 0) {
            for (IResult r : items) {
                if (r.getStatus().isError()) {
                    r.setStatus(Success.INSTANCE);
                }
            }
        } else {
            try {
                ISource source = context.getSources().getLast();
                Document document = source.getDocument();
                Element root = document.getRootElement();
                addResult(Failure.INSTANCE, context.newBlock(root, null), new Exception(errors.toString()));
            } catch (SourceException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Append messages to error list.
     * 
     * @param errors
     *            A error string.
     * @param list
     *            A error list.
     */
    protected void append(StringBuilder errors, List<String> list) {
        for (String s : list) {
            errors.append('\t');
            errors.append(s);
        }
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
    public Status getStatus() {
        Status result = Success.INSTANCE;
        for (IResult s : this) {
            result = result.max(s.getStatus());
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Status> List<T> availableStatus() {
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
        return filterByStatus(subList(start, end), status);
    }

    @Override
    public <T extends Status> List<IResult> filterByStatus(List<IResult> subset, T... status) {
        List<IResult> result = new LinkedList<IResult>();
        Set<Status> valid = new HashSet<Status>();
        for (int i = 0; i < status.length; i++) {
            valid.add(status[i]);
        }
        for (IResult t : subset) {
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
    public <T extends Status> int countStatus(List<IResult> subset, T... status) {
        return filterByStatus(subset, status).size();
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
        status = analyzeStatus(status, message, failure);
        if (!recordSuccess && !status.isError()) {
            return null;
        }
        IResult result = new ResultImpl(status, source, message, failure, writable);
        if (add(result)) {
            return result;
        }
        return null;
    }

    /**
     * Perform status analysis based on expected message.
     * 
     * @param status
     *            The status.
     * @param message
     *            The message.
     * @param failure
     *            The failure.
     * @return The adjusted status.
     */
    protected Status analyzeStatus(Status status, String message, Throwable failure) {
        if (messages != null && status.isError()) {
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

    @Override
    public IResultSet subSet(int start, int end) {
        ResultSetImpl subset = new ResultSetImpl();
        subset.addAll(this.subList(start, end));
        return subset;
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

        td = new Element("th");
        td.addAttribute(new Attribute("class", "sr_nop"));
        tr.appendChild(td);
        td.appendChild("");

        td = new Element("th");
        td.addAttribute(new Attribute("class", "sr_nop"));
        tr.appendChild(td);
        td.appendChild("");

        List<Status> available = availableStatus();
        for (Status s : available) {
            td = new Element("th");
            tr.appendChild(td);
            td.addAttribute(new Attribute("class", s.getCssName()));
            td.appendChild(s.asNode());

            td = new Element("th");
            tr.appendChild(td);
            td.addAttribute(new Attribute("class", "sr_small"));
            td.appendChild("[" + countStatus(s) + "]");
        }
        int index = 0;
        for (ActionType t : actionTypes()) {
            tr = new Element("tr");
            tr.addAttribute(new Attribute("class", (index++ % 2 == 0 ? "sr_even" : "sr_odd")));
            table.appendChild(tr);

            td = new Element("td");
            tr.appendChild(td);
            td.appendChild(t.asNode());

            td = new Element("td");
            tr.appendChild(td);
            td.addAttribute(new Attribute("class", "sr_resultsetn"));
            td.appendChild("[" + countType(t) + "]");

            List<IResult> filter = filterByType(t);
            for (Status s : available) {
                td = new Element("td");
                td.addAttribute(new Attribute("colspan", "2"));
                td.addAttribute(new Attribute("class", "sr_resultsetn"));
                tr.appendChild(td);
                td.appendChild(String.valueOf(countStatus(filter, s)));
            }
        }
        return table;
    }
}