package org.specrunner.junit;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Factory of threads named.
 * 
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * 
 */
public class NamedFactory implements ThreadFactory {
    /**
     * Pool number.
     */
    private final AtomicInteger poolNumber = new AtomicInteger(1);
    /**
     * Thread number.
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * Thread group.
     */
    private final ThreadGroup group;

    /**
     * Friendly constructor.
     * 
     * @param poolName
     *            The pool name.
     */
    public NamedFactory(String poolName) {
        group = new ThreadGroup(poolName + "-" + poolNumber.getAndIncrement());
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(group, r, group.getName() + "-thread-" + threadNumber.getAndIncrement(), 0);
    }
}
