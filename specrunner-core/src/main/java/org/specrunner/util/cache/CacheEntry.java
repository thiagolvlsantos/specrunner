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
 * A cache entry.
 * 
 * @author Thiago Santos.
 * 
 * @param <K>
 *            Key type.
 * @param <T>
 *            The object cache type.
 */
public class CacheEntry<K, T> implements Comparable<CacheEntry<K, T>> {

    /**
     * The time flat of this item.
     */
    private long timestamp;
    /**
     * The cache key.
     */
    private K key;
    /**
     * The object value.
     */
    private T value;

    /**
     * Basic constructor.
     * 
     * @param key
     *            The key.
     * @param value
     *            The value.
     */
    public CacheEntry(K key, T value) {
        timestamp = System.currentTimeMillis();
        this.key = key;
        this.value = value;
    }

    /**
     * Get the cache key.
     * 
     * @return The key.
     */
    public K getKey() {
        return key;
    }

    /**
     * Get the value.
     * 
     * @return The value.
     */
    public T getValue() {
        return value;
    }

    /**
     * Renew cache item timestamp.
     */
    public void renew() {
        timestamp = System.currentTimeMillis();
    }

    /**
     * Based on timeout, says if an element must be removed from cache.
     * 
     * @param timeout
     *            The timeout.
     * @return true, if out of date, true, otherwise.
     */
    public boolean invalid(long timeout) {
        return timestamp + timeout < System.currentTimeMillis();
    }

    @Override
    public int compareTo(CacheEntry<K, T> o) {
        return (int) (timestamp - o.timestamp);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        return obj instanceof CacheEntry ? key.equals(((CacheEntry) obj).key) : false;
    }
}
