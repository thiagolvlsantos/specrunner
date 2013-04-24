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
     * @param <T>
     *            Cache object type.
     * @param name
     *            Cache name.
     * @return A cache.
     */
    <T> ICache<T> newCache(String name);
}
