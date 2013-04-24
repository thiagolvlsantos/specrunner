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
 * @param <T>
 *            The cache object type.
 */
public class CacheLRU<T> implements ICache<T> {

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
    private Map<String, CacheEntry<T>> items = new HashMap<String, CacheEntry<T>>();

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
    public ICache<T> setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ICache<T> setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public ICache<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public ICache<T> setClean(long clean) {
        this.clean = clean;
        return this;
    }

    @Override
    public T get(String key) {
        CacheEntry<T> item = items.get(key);
        if (item != null) {
            if (!item.invalid(timeout)) {
                item.renew();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Cache '" + name + "' hit: " + key);
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
    public ICache<T> put(String key, T value) {
        if (items.size() > size) {
            Set<CacheEntry<T>> set = new TreeSet<CacheEntry<T>>(items.values());
            int index = 0;
            Iterator<CacheEntry<T>> ite = set.iterator();
            while (index++ < clean && ite.hasNext()) {
                CacheEntry<T> next = ite.next();
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Cache '" + name + "' clean: " + key);
                }
                items.remove(next.getKey());
            }
        }
        items.put(key, new CacheEntry<T>(key, value));
        return this;
    }
}
