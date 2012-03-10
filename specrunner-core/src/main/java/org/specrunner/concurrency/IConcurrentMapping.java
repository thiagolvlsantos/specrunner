package org.specrunner.concurrency;

/**
 * Given a resource by name, returns the equivalent within a concurrent
 * environment, for example, when asked to a database url as
 * <code>IConcurrentMapping.get("url","jdbc:hsqld:mem:test")</code> could return
 * <code>"jdbc:hsqld:mem:testThread1"</code>.
 * 
 * @author Thiago Santos
 * 
 */
public interface IConcurrentMapping {

    /**
     * Given a resource name and the original one, return a concurrent version
     * of this resource.
     * 
     * @param name
     *            The resource name.
     * @param value
     *            The resource value.
     * @return The changed resource.
     */
    Object get(String name, Object value);

    /**
     * Get the thread name normalized.
     * 
     * @return The thread name.
     */
    String getThread();
}