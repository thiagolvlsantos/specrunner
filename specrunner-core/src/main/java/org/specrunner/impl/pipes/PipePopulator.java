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
package org.specrunner.impl.pipes;

import org.specrunner.SpecRunnerException;
import org.specrunner.SpecRunnerServices;
import org.specrunner.context.ContextException;
import org.specrunner.context.IContext;
import org.specrunner.context.IContextPopulator;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;

/**
 * Add extra information to the given context.
 * 
 * @author Thiago Santos
 * 
 */
public class PipePopulator implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            IContext context = PipeContext.lookup(channel);
            PipeContext.bind(channel, populate(context));
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Populate a context with predefined values.
     * 
     * @param context
     *            The context to be populated.
     * @return The context populated.
     * @throws ContextException
     *             On populate errors.
     */
    protected IContext populate(IContext context) throws ContextException {
        return SpecRunnerServices.get(IContextPopulator.class).populate(context);
    }
}
