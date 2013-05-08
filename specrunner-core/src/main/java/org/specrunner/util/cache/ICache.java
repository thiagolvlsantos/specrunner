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
}