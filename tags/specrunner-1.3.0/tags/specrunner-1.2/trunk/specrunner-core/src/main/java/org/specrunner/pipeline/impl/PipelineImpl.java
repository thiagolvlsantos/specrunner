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
package org.specrunner.pipeline.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.IPipeListener;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.PipelineException;

/**
 * Default implementation of a pipeline.
 * 
 * @author Thiago Santos
 */
@SuppressWarnings("serial")
public class PipelineImpl extends LinkedList<IPipe> implements IPipeline {

    /**
     * List of listeners.
     */
    protected List<IPipeListener> listeners = new LinkedList<IPipeListener>();

    /**
     * Superclass constructor.
     */
    public PipelineImpl() {
        super();
    }

    /**
     * Superclass constructor.
     * 
     * @param c
     *            A set of pipes.
     */
    public PipelineImpl(Collection<? extends IPipe> c) {
        super(c);
    }

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws AbortException, PipelineException {
        for (IPipe p : this) {
            boolean proceed = true;
            try {
                fireBeforeCheck(channel, p);
                proceed = p.check(channel);
            } finally {
                fireAfterCheck(channel, p);
            }
            if (proceed) {
                try {
                    fireBeforePerform(channel, p);
                    p.process(channel);
                } finally {
                    fireAfterPerform(channel, p);
                }
            }
        }
        return channel;
    }

    @Override
    public void addPipelineListener(IPipeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removePipelineListener(IPipeListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    /**
     * Fire event on channel.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source pipe.
     */
    protected void fireBeforeCheck(IChannel channel, IPipe source) {
        for (IPipeListener p : listeners) {
            p.onBeforeCheck(channel, source);
        }
    }

    /**
     * Fire event on channel.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source pipe.
     */
    protected void fireAfterCheck(IChannel channel, IPipe source) {
        for (IPipeListener p : listeners) {
            p.onAfterCheck(channel, source);
        }
    }

    /**
     * Fire event on channel.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source pipe.
     */
    protected void fireBeforePerform(IChannel channel, IPipe source) {
        for (IPipeListener p : listeners) {
            p.onBeforeProcess(channel, source);
        }
    }

    /**
     * Fire event on channel.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source pipe.
     */
    protected void fireAfterPerform(IChannel channel, IPipe source) {
        for (IPipeListener p : listeners) {
            p.onAfterProcess(channel, source);
        }
    }
}