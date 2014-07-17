/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.pipeline.core;

import org.specrunner.pipeline.IPipe;
import org.specrunner.pipeline.IPipeline;
import org.specrunner.pipeline.IPipelineFactory;
import org.specrunner.pipeline.PipelineException;

/**
 * Default implementation of a pipeline factory.
 * 
 * @author Thiago Santos
 */
public class PipelineFactoryImpl implements IPipelineFactory {

    @Override
    public IPipeline newPipeline(Object source) throws PipelineException {
        return newPipeline();
    }

    @Override
    public IPipeline newPipeline(IPipe... pipes) {
        IPipeline result = new PipelineImpl();
        if (pipes != null) {
            for (IPipe p : pipes) {
                result.add(p);
            }
        }
        return result;
    }
}