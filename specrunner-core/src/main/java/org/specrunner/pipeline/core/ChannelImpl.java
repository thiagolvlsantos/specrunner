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
package org.specrunner.pipeline.core;

import java.util.HashMap;
import java.util.Map;

import org.specrunner.pipeline.IChannel;
import org.specrunner.pipeline.InvalidTypeException;
import org.specrunner.pipeline.NotFoundException;

/**
 * Default implementation of a channel.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ChannelImpl extends HashMap<String, Object> implements IChannel {

    /**
     * Super class constructor.
     */
    public ChannelImpl() {
        super();
    }

    /**
     * Super class constructor.
     * 
     * @param initialCapacity
     *            see new Map(initialCapacity,loadFactor)
     * @param loadFactor
     *            see new Map(initialCapacity,loadFactor)
     */
    public ChannelImpl(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Super class constructor.
     * 
     * @param initialCapacity
     *            see new Map(initialCapacity,loadFactor)
     */
    public ChannelImpl(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Super class constructor.
     * 
     * 
     * @param m
     *            see new Map(m)
     */
    public ChannelImpl(Map<? extends String, ? extends Object> m) {
        super(m);
    }

    @Override
    public IChannel add(String key, Object value) {
        put(key, value);
        return this;
    }

    @Override
    public <T> T get(String key, Class<T> type) throws NotFoundException, InvalidTypeException {
        Object obj = get(key);
        if (obj == null) {
            throw new NotFoundException("Channel does not contain '" + key + "'.");
        }
        if (type == null) {
            throw new NotFoundException("ActionType information cannot be null.");
        }
        Class<? extends Object> clazz = obj.getClass();
        if (!type.isAssignableFrom(clazz)) {
            throw new NotFoundException("ActionType missmatch for key '" + key + "' expected='" + type + "', received='" + clazz + "'.");
        }
        return type.cast(obj);
    }
}
