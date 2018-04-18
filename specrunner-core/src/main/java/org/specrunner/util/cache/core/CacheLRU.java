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
package org.specrunner.util.cache.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.CacheEntry;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheCleaner;

/**
 * LRU implementation of a cache.
 * 
 * @author Thiago Santos
 * 
 * @param <K>
 *            Key type.
 * @param <T>
 *            The cache object type.
 */
public class CacheLRU<K, T> implements ICache<K, T> {

    /**
     * Cache name.
     */
    private String name;
    /**
     * Cache timeout.
     */
    private long timeout = ICache.DEFAULT_FEATURE_TIMEOUT;
    /**
     * Cache size.
     */
    private long size = ICache.DEFAULT_FEATURE_SIZE;
    /**
     * Cache clean size.
     */
    private long clean = ICache.DEFAULT_FEATURE_CLEAN;
    /**
     * Map of items.
     */
    private Map<K, CacheEntry<K, T>> items = new HashMap<K, CacheEntry<K, T>>();

    /**
     * Object cleaner.
     */
    private ICacheCleaner<T> cleaner;

    /**
     * Basic constructor.
     * 
     * @param name
     *            The cache name.
     * @param cleaner
     *            A cleaner.
     */
    public CacheLRU(String name, ICacheCleaner<T> cleaner) {
        this.name = name;
        this.cleaner = cleaner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ICache<K, T> setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ICacheCleaner<T> getCleaner() {
        return cleaner;
    }

    @Override
    public ICache<K, T> setCleaner(ICacheCleaner<T> cleaner) {
        this.cleaner = cleaner;
        return this;
    }

    @Override
    public ICache<K, T> setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public ICache<K, T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public ICache<K, T> setClean(long clean) {
        this.clean = clean;
        return this;
    }

    @Override
    public T get(K key) {
        CacheEntry<K, T> item = items.get(key);
        if (item != null) {
            if (!item.invalid(timeout)) {
                item.renew();
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Cache '" + name + "' hit: " + key);
                }
                return item.getValue();
            } else {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Cache '" + name + "' expired: " + key);
                }
                cleaner.destroy(items.remove(item.getKey()).getValue());
            }
        }
        return null;
    }

    @Override
    public ICache<K, T> put(K key, T value) {
        if (items.size() > size) {
            Set<CacheEntry<K, T>> set = new TreeSet<CacheEntry<K, T>>(items.values());
            int index = 0;
            Iterator<CacheEntry<K, T>> ite = set.iterator();
            while (index++ < clean && ite.hasNext()) {
                CacheEntry<K, T> next = ite.next();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Cache '" + name + "' clean: " + next.getKey());
                }
                remove(next.getKey());
            }
        }
        items.put(key, new CacheEntry<K, T>(key, value));
        return this;
    }

    @Override
    public void remove(K key) {
        CacheEntry<K, T> entry = items.remove(key);
        if (entry != null) {
            cleaner.destroy(entry.getValue());
        }
    }

    @Override
    public void release() {
        for (CacheEntry<K, T> entry : items.values()) {
            cleaner.destroy(entry.getValue());
        }
        items.clear();
    }
}
