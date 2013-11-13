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
package org.specrunner.pipeline;

/**
 * Stand for a stage (pipe) in a pipeline process.
 * 
 * @author Thiago Santos
 */
public interface IPipe {

    /**
     * Check if a pipe must perform something on channel. If the channel is
     * corrupted the pipe must throw AbortException.
     * 
     * @param channel
     *            The information channel.
     * @return true, if pipe must perform something on channel, false,
     *         otherwise.
     * @throws AbortException
     *             If the channel is corrupted.
     */
    boolean check(IChannel channel) throws AbortException;

    /**
     * Perform some transformation over the channel.
     * 
     * @param channel
     *            The channel.
     * @return The pipe itself.
     * @throws AbortException
     *             On processing errors.
     * @throws PipelineException
     *             On processing errors.
     */
    IChannel process(IChannel channel) throws AbortException, PipelineException;
}