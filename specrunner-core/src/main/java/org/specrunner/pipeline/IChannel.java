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

import java.util.Map;


/**
 * Abstraction of a channel where information can be obtained from and written
 * to.
 * 
 * @author Thiago Santos
 * 
 */
public interface IChannel extends Map<String, Object>, IChannelAware {

    /**
     * Add data to the channel.
     * 
     * @param key
     *            The data key.
     * @param value
     *            The data value.
     * @return The channel itself.
     */
    IChannel add(String key, Object value);

    /**
     * @param <T>
     *            The expected type.
     * @param key
     *            The object name.
     * @param type
     *            The object type.
     * @return The object instance.
     * @throws NotFoundException
     *             If key is not present.
     * @throws InvalidTypeException
     *             If type does not match.
     */
    <T> T get(String key, Class<T> type) throws NotFoundException, InvalidTypeException;
}