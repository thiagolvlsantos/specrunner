/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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

import org.specrunner.SpecRunnerException;
import org.specrunner.context.IContext;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.result.IResultSet;
import org.specrunner.runner.IRunner;
import org.specrunner.source.ISource;

/**
 * Call a specification runner.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeRun implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            IRunner runner = PipeRunner.lookup(channel);
            ISource source = PipeSource.lookup(channel);
            IContext context = PipeContext.lookup(channel);
            IResultSet result = PipeResult.lookup(channel);
            runner.run(source, context, result);
            result.consolidate(context);
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }
}
