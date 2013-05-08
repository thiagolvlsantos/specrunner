package org.specrunner.util.cache.impl;

import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Abstract implementation of a factory.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractCacheFactory implements ICacheFactory {

    @Override
    public <K, T> ICache<K, T> newCache(String name) {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        CacheLRU<K, T> cache = create(name);
        fm.set(ICache.FEATURE_TIMEOUT, cache);
        fm.set(ICache.FEATURE_SIZE, cache);
        fm.set(ICache.FEATURE_CLEAN, cache);
        return cache;
    }

    /**
     * Create the cache object.
     * 
     * @param <K>
     *            Key type.
     * @param <T>
     *            Cache object type.
     * @param name
     *            Cache name.
     * @return The cache instance.
     */
    protected abstract <K, T> CacheLRU<K, T> create(String name);
}