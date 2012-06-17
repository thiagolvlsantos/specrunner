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

import java.io.IOException;

import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.context.IBlock;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.type.Undefined;
import org.specrunner.result.IResult;
import org.specrunner.result.IWritable;
import org.specrunner.result.Status;
import org.specrunner.util.ExceptionUtil;
import org.specrunner.util.UtilLog;

/**
 * Default result implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ResultImpl implements IResult {

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
    public IBlock getBlock() {
        return source;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getFailure() {
        return failure;
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
            msg1 = ". on " + plugin + "[" + plugin.getAllParameters() + "]";
        }

        String msg2 = getFailure() != null ? getFailure().getMessage() : getMessage();
        if (UtilLog.LOG.isDebugEnabled() && getFailure() != null) {
            try {
                msg2 += "\n" + ExceptionUtil.toString(getFailure());
            } catch (IOException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
            }
        }
        return msg1 + msg2;
    }

    @Override
    public Node asNode() {
        return new Text(asString());
    }

    @Override
    public String toString() {
        return status.getName() + "(" + source.getNode() != null ? source.getNode().toXML() : String.valueOf(source.getPlugin()) + "," + message + (failure != null ? "," + failure.getMessage() : "") + ")." + writable;
    }
}