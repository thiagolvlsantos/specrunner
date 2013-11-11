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

import java.io.File;
import java.util.Map;

import org.specrunner.SpecRunnerException;
import org.specrunner.SRServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.pipeline.AbortException;
import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.PipelineException;
import org.specrunner.result.IResultSet;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;

/**
 * Make dumpers dump. :)
 * 
 * @author Thiago Santos
 * 
 */
public class PipeDump implements IPipe {

    @Override
    public boolean check(IChannel channel) throws AbortException {
        return true;
    }

    @Override
    public IChannel process(IChannel channel) throws PipelineException {
        try {
            // message before dump
            messageBefore(PipeInput.lookup(channel));

            // dump results
            IResultSet result = PipeResult.lookup(channel);
            Map<String, Object> model = PipeModel.recover(channel);

            PipeDumper.lookup(channel).dump(PipeSource.lookup(channel), result, model);

            // message after dump
            messageAfter(model, result);
        } catch (SpecRunnerException e) {
            throw new PipelineException(e);
        }
        return channel;
    }

    /**
     * Print message before execution.
     * 
     * @param input
     *            The input.
     */
    protected void messageBefore(String input) {
        IOutput out = SRServices.get(IOutputFactory.class).currentOutput();
        out.println("");
        out.println(" Input " + getNome() + ": " + (input != null ? input.replace('/', File.separatorChar) : "null"));
    }

    /**
     * Thread normalized name.
     * 
     * @return The normalized thread named.
     */
    protected String getNome() {
        return "(" + SRServices.get(IConcurrentMapping.class).getThread() + ")";
    }

    /**
     * Message after execution.
     * 
     * @param info
     *            The model information.
     * @param result
     *            The result set.
     */
    protected void messageAfter(Map<String, Object> info, IResultSet result) {
        IOutput out = SRServices.get(IOutputFactory.class).currentOutput();
        out.printf("Result " + getNome() + ": %s \n", result.asString());
        out.printf("    In " + getNome() + ": %d ms \n", info.get(PipeTime.TIME));
        out.printf("    At " + getNome() + ": %s \n", info.get(PipeTimestamp.DATE));
    }
}
