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

import java.util.Map.Entry;

import org.specrunner.context.IContext;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Bind pipe information to the channel.
 * 
 * @author Thiago Santos
 * 
 */
public class PipeMetavariables implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        IContext context = PipeContext.lookup(channel);
        for (Entry<String, Object> e : channel.entrySet()) {
            String name = UtilEvaluator.asVariable("$" + e.getKey().toUpperCase());
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Metavariable(" + name + ")=" + e.getValue());
            }
            context.saveGlobal(name, e.getValue());
        }
        return channel;
    }
}