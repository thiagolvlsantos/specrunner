package org.specrunner.concurrency.impl;

import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.util.UtilLog;
import org.specrunner.util.UtilString;

/**
 * Default implementation of IConcurrentMapping, returns a String concatenated
 * with thread name, does not matter the resource name.
 * 
 * @author Thiago Santos.
 * 
 */
public class ConcurrentMappingImpl implements IConcurrentMapping {

    @Override
    public Object get(String name, Object value) {
        String result = String.valueOf(value) + getThread();
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Mapping '" + name + "'='" + value + "' to '" + result);
        }
        return result;
    }

    @Override
    public String getThread() {
        return UtilString.normalize(Thread.currentThread().getName()).replace("-", "");
    }
}