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
package org.specrunner.pipeline;

/**
 * Stand for a channel factory.
 * 
 * @author Thiago Santos
 */
public interface IPipelineFactory {

    /**
     * A new pipeline from source object.
     * 
     * @param source
     *            The pipeline source.
     * @return A pipeline.
     * @throws PipelineException
     *             A pipeline.
     */
    IPipeline newPipeline(Object source) throws PipelineException;

    /**
     * A new pipeline.
     * 
     * @param pipes
     *            A, possibly, empty list of pipes.
     * @return A pipeline.
     * @throws PipelineException
     *             On creation errors.
     */
    IPipeline newPipeline(IPipe... pipes) throws PipelineException;
}