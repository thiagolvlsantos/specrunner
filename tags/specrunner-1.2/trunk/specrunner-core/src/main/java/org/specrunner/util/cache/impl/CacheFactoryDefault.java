package org.specrunner.util.cache.impl;

/**
 * Default implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class CacheFactoryDefault extends AbstractCacheFactory {

    @Override
    protected <K, T> CacheLRU<K, T> create(String name) {
        return new CacheLRU<K, T>(name);
    }
}