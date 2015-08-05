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
package org.specrunner.core.pipes;

import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;
import org.specrunner.context.IContextFactory;
import org.specrunner.context.IContextListener;
import org.specrunner.listeners.IListenerManager;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.runner.IRunner;
import org.specrunner.source.ISource;

/**
 * Create and bind a context to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeContext implements IPipe {

    /**
     * Source.
     */
    public static final String CONTEXT = "context";

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // for doRun(IPlugin m,...), the source cannot be present.
            ISource source = (ISource) channel.get(PipeSource.SOURCE);
            // a new context
            IRunner runner = PipeRunner.lookup(channel);
            bind(channel, createContext(source, runner));
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Creates a context based on a source and runner.
     * 
     * @param source
     *            The input.
     * @param runner
     *            The runner.
     * @return Context errors.
     * @throws ContextException
     *             On context creation.
     */
    protected IContext createContext(ISource source, IRunner runner) throws ContextException {
        IContext context = SRServices.get(IContextFactory.class).newContext(source, runner);
        for (IContextListener listener : SRServices.get(IListenerManager.class).filterByType(IContextListener.class)) {
            listener.onCreate(context);
        }
        return context;
    }

    /**
     * Bind the object to the channel.
     * 
     * @param channel
     *            The channel.
     * @param obj
     *            The object.
     * @return The channel itself.
     */
    public static IChannel bind(IChannel channel, IContext obj) {
        return channel.add(CONTEXT, obj);
    }

    /**
     * Recover a context from channel.
     * 
     * @param channel
     *            The channel.
     * @return The context.
     * @throws NotFoundException
     *             On lookup errors.
     * @throws InvalidTypeException
     *             On type errors.
     */
    public static IContext lookup(IChannel channel) throws NotFoundException, InvalidTypeException {
        return channel.get(CONTEXT, IContext.class);
    }
}
