package org.specrunner.util.cache;

/**
 * A cache entry.
 * 
 * @author Thiago Santos.
 * 
 * @param <T>
 *            The object cache type.
 */
public class CacheEntry<T> implements Comparable<CacheEntry<T>> {

    /**
     * The time flat of this item.
     */
    private long timestamp;
    /**
     * The cache key.
     */
    private String key;
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
    public CacheEntry(String key, T value) {
        timestamp = System.currentTimeMillis();
        this.key = key;
        this.value = value;
    }

    /**
     * Get the cache key.
     * 
     * @return The key.
     */
    public String getKey() {
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
    public int compareTo(CacheEntry<T> o) {
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