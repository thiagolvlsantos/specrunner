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
}
