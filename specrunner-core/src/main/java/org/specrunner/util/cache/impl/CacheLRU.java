package org.specrunner.util.cache.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.CacheEntry;
import org.specrunner.util.cache.ICache;

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
     * Basic constructor.
     * 
     * @param name
     *            The cache name.
     */
    public CacheLRU(String name) {
        this.name = name;
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
    public boolean contains(K key) {
        return items.containsKey(key);
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
                items.remove(item.getKey());
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
                items.remove(next.getKey());
            }
        }
        items.put(key, new CacheEntry<K, T>(key, value));
        return this;
    }
}
