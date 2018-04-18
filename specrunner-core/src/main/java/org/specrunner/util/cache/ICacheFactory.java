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
package org.specrunner.util.cache;

/**
 * Abstraction for cache builders.
 * 
 * @author Thiago Santos
 * 
 */
public interface ICacheFactory {

    /**
     * Creates a new cache.
     * 
     * @param <K>
     *            Key type.
     * @param <T>
     *            Cache object type.
     * @param name
     *            Cache name.
     * @return A cache.
     */
    <K, T> ICache<K, T> newCache(String name);

    /**
     * Creates a new cache.
     * 
     * @param <K>
     *            Key type.
     * @param <T>
     *            Cache object type.
     * @param name
     *            Cache name.
     * @param cleaner
     *            A cleaner of objects in cache when it overloads.
     * @return A cache.
     */
    <K, T> ICache<K, T> newCache(String name, ICacheCleaner<T> cleaner);
}
