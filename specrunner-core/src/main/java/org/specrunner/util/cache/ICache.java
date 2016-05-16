/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
 * Cache definition.
 * 
 * @author Thiago Santos
 * 
 * @param <K>
 *            Key type.
 * @param <T>
 *            The cache object type.
 */
public interface ICache<K, T> {

    /**
     * Get the cache name.
     * 
     * @return The name.
     */
    String getName();

    /**
     * Set a cache name.
     * 
     * @param name
     *            A cache.
     * @return The cache itself.
     */
    ICache<K, T> setName(String name);

    /**
     * Get the cache cleaner.
     * 
     * @return The cleaner.
     */
    ICacheCleaner<T> getCleaner();

    /**
     * Set the cleaner.
     * 
     * @param cleaner
     *            A cleaner.
     * @return The cache itself.
     */
    ICache<K, T> setCleaner(ICacheCleaner<T> cleaner);

    /**
     * Feature for cache timeout.
     */
    String FEATURE_TIMEOUT = ICache.class.getName() + ".timeout";
    /**
     * Five minutes cache.
     */
    long DEFAULT_FEATURE_TIMEOUT = 5 * 60 * 1000;

    /**
     * Set cache elements timeout.
     * 
     * @param timeout
     *            The timeout.
     * @return The cache itself.
     */
    ICache<K, T> setTimeout(long timeout);

    /**
     * Feature for cache size.
     */
    String FEATURE_SIZE = ICache.class.getName() + ".size";
    /**
     * 100 elements cache.
     */
    long DEFAULT_FEATURE_SIZE = 100;

    /**
     * Set cache max size.
     * 
     * @param size
     *            The size.
     * @return The cache itself.
     */
    ICache<K, T> setSize(long size);

    /**
     * Feature for cache clean.
     */
    String FEATURE_CLEAN = ICache.class.getName() + ".clean";
    /**
     * Clean 50 elements.
     */
    long DEFAULT_FEATURE_CLEAN = 50;

    /**
     * Set cache number of removed elements on cache overload.
     * 
     * @param clean
     *            The number of items to be removed.
     * @return The cache itself.
     */
    ICache<K, T> setClean(long clean);

    /**
     * Says if a key is mapped.
     * 
     * @param key
     *            The key.
     * @return true, if mapped, false, otherwise.
     */
    boolean contains(K key);

    /**
     * Get a cache object.
     * 
     * @param key
     *            The object key.
     * @return A object, if an entry exists and is not out of date, null,
     *         otherwise.
     */
    T get(K key);

    /**
     * Put an element to the cache.
     * 
     * @param key
     *            The key.
     * @param value
     *            The object value.
     * @return The cache itself.
     */
    ICache<K, T> put(K key, T value);

    /**
     * Remove an object from cache.
     * 
     * @param key
     *            The key.
     */
    void remove(K key);

    /**
     * Release cache.
     */
    void release();
}
