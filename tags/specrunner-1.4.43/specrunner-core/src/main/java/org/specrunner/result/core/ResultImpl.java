/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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

import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.context.IBlock;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResult;
import org.specrunner.result.IWritable;
import org.specrunner.result.Status;
import org.specrunner.util.UtilException;
import org.specrunner.util.xom.IPresentation;

/**
 * Default result implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ResultImpl implements IResult {

    /**
     * Get full trace mode.
     */
    private Boolean fullTrace = true;
    /**
     * The status.
     */
    protected Status status;
    /**
     * The source block.
     */
    protected IBlock source;
    /**
     * A associated message.
     */
    protected String message;
    /**
     * The associated failure.
     */
    protected Throwable failure;
    /**
     * The associated writable information.
     */
    protected IWritable writable;

    @Override
    public Boolean getFullTrace() {
        return fullTrace;
    }

    @Override
    public void setFullTrace(Boolean fullTrace) {
        this.fullTrace = fullTrace;
    }

    /**
     * The result instance creator.
     * 
     * @param status
     *            The status.
     * @param source
     *            The source block.
     * @param message
     *            The message.
     * @param failure
     *            The failure.
     * @param writable
     *            The writable resources.
     */
    public ResultImpl(Status status, IBlock source, String message, Throwable failure, IWritable writable) {
        this.status = status;
        this.source = source;
        this.message = message;
        this.failure = failure;
        this.writable = writable;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean hasBlock() {
        return getBlock() != null;
    }

    @Override
    public IBlock getBlock() {
        return source;
    }

    @Override
    public boolean hasMessage() {
        return getMessage() != null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean hasFailure() {
        return getFailure() != null;
    }

    @Override
    public Throwable getFailure() {
        return failure;
    }

    @Override
    public boolean hasWritable() {
        return getWritable() != null;
    }

    @Override
    public IWritable getWritable() {
        return writable;
    }

    @Override
    public ActionType getActionType() {
        if (source != null) {
            IPlugin p = source.getPlugin();
            if (p != null) {
                return p.getActionType();
            }
        }
        return Undefined.INSTANCE;
    }

    @Override
    public String asString() {
        String msg1 = "";
        if (!source.hasNode()) {
            IPlugin plugin = source.getPlugin();
            msg1 = ". on " + plugin + "[" + plugin.getParameters().getAllParameters() + "]";
        }

        Throwable failure1 = getFailure();
        String msg2 = hasFailure() ? UtilException.toString(failure1, fullTrace) : getMessage();
        if (hasFailure()) {
            Throwable failure2 = UtilException.unwrapPresentation(failure1);
            if (failure1 != failure2) {
                msg2 += "\n" + UtilException.toString(failure2, fullTrace);
            }
        }
        return msg1 + msg2;
    }

    @Override
    public Node asNode() {
        Throwable out = UtilException.unwrapPresentation(getFailure());
        if (out instanceof IPresentation) {
            return ((IPresentation) out).asNode();
        }
        return new Text(asString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(status.getName());
        sb.append("(");
        if (source.hasNode()) {
            sb.append(source.getNode().toXML());
        } else {
            sb.append(source.getPlugin());
        }
        sb.append(",");
        sb.append(message);
        if (failure != null) {
            sb.append("," + failure.getMessage());
        }
        sb.append(")." + writable);
        return sb.toString();
    }
}
