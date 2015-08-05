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
package org.specrunner.pipeline;

/**
 * A listener of pipes.
 * 
 * @author Thiago Santos
 * 
 */
public interface IPipeListener {

    /**
     * Perform something before checking.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source.
     */
    void onBeforeCheck(IChannel channel, IPipe source);

    /**
     * Perform something after checking.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source.
     */
    void onAfterCheck(IChannel channel, IPipe source);

    /**
     * Perform something before processing.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source.
     */
    void onBeforeProcess(IChannel channel, IPipe source);

    /**
     * Perform something after processing.
     * 
     * @param channel
     *            The channel.
     * @param source
     *            The source.
     */
    void onAfterProcess(IChannel channel, IPipe source);
}
