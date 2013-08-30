package org.specrunner.util.mapping.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
// CHECKSTYLE: OFF
import java.util.Map.Entry;
//CHECKSTYLE: ON
import java.util.Properties;

import org.specrunner.SpecRunnerServices;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.util.UtilLog;
import org.specrunner.util.mapping.IMappingManager;
import org.specrunner.util.mapping.IResetable;

/**
 * Default implementation of managers loaded by property files.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            The mapped object type.
 */
@SuppressWarnings("serial")
public abstract class MappingManagerImpl<T extends IResetable> extends HashMap<String, T> implements IMappingManager<T> {
    /**
     * File to load.
     */
    private String file;

    /**
     * Initialization flag.
     */
    protected boolean initialized = false;

    /**
     * Default constructor.
     * 
     * @param file
     *            The mapping file.
     */
    public MappingManagerImpl(String file) {
        this.file = file;
    }

    /**
     * Initialize manager.
     */
    public void initialize() {
        if (!initialized) {
            try {
                Properties p = SpecRunnerServices.get(IPropertyLoader.class).load(file);
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("properties=" + p);
                }
                List<Entry<Object, Object>> sorted = new ArrayList<Entry<Object, Object>>(p.entrySet());
                // sorting by size, makes alias free mapping be performed first.
                Collections.sort(sorted, new Comparator<Entry<Object, Object>>() {
                    @Override
                    public int compare(java.util.Map.Entry<Object, Object> o1, java.util.Map.Entry<Object, Object> o2) {
                        return String.valueOf(o2.getValue()).length() - String.valueOf(o1.getValue()).length();
                    }
                });
                for (Entry<Object, Object> e : sorted) {
                    String key = String.valueOf(e.getKey());
                    String property = p.getProperty(key);
                    T instance = super.get(property);
                    if (instance == null) {
                        @SuppressWarnings("unchecked")
                        Class<? extends T> c = (Class<? extends T>) Class.forName(property);
                        instance = c.newInstance();
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("put(" + key + "," + instance + "[of type " + c + "])");
                        }
                    } else {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("reuse.put(" + key + "," + instance + ")");
                        }
                    }
                    put(key, instance);
                }
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
            initialized = true;
        }
    }

    @Override
    public IMappingManager<T> bind(String name, T obj) {
        initialize();
        put(name, obj);
        return this;
    }

    @Override
    public T get(Object name) {
        initialize();
        T c = super.get(name);
        if (c != null) {
            c.initialize();
        }
        return c;
    }

    @Override
    public T getDefault() {
        return get(DEFAULT_NAME);
    }
}
