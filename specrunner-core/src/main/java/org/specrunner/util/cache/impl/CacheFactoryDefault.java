package org.specrunner.util.cache.impl;

/**
 * Default implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class CacheFactoryDefault extends AbstractCacheFactory {

    @Override
    protected <T> CacheLRU<T> create(String name) {
        return new CacheLRU<T>(name);
    }
}