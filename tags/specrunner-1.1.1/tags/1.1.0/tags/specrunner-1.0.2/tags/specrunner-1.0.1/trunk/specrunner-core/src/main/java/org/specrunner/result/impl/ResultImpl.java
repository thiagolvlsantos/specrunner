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

import org.specrunner.context.IBlock;
import org.specrunner.result.IResult;
import org.specrunner.result.IWritable;
import org.specrunner.result.Status;

/**
 * Default result implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ResultImpl implements IResult {

    protected Status status;
    protected IBlock source;
    protected String message;
    protected Throwable failure;
    protected IWritable writable;

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
    public String toString() {
        return status.getName() + "(" + source.getNode() != null ? source.getNode().toXML() : String.valueOf(source.getPlugin()) + "," + message + (failure != null ? "," + failure.getMessage() : "") + ")." + writable;
    }
}