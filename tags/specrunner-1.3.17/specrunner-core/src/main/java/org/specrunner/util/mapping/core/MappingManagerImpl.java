package org.specrunner.util.mapping.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.specrunner.SRServices;
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
                long time = System.currentTimeMillis();
                List<Properties> list = SRServices.get(IPropertyLoader.class).load(file);
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("properties list=" + list);
                }
                for (Properties p : list) {
                    Map<String, T> instances = new HashMap<String, T>();
                    for (Entry<Object, Object> e : p.entrySet()) {
                        String key = String.valueOf(e.getKey());
                        String property = p.getProperty(key);
                        String keyNormalized = normalizeKey(key);
                        T instance = instances.get(property);
                        if (instance == null) {
                            @SuppressWarnings("unchecked")
                            Class<? extends T> c = (Class<? extends T>) Class.forName(property);
                            instance = c.newInstance();
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug("put(" + keyNormalized + "," + instance + "[of type " + c + "])");
                            }
                        } else {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug("reuse.put(" + keyNormalized + "," + instance + ")");
                            }
                        }
                        put(keyNormalized, instance);
                        instances.put(property, instance);
                    }
                }
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("LOAD TIME(" + file + "):" + (System.currentTimeMillis() - time));
                }
            } catch (Exception e) {
                throw new ExceptionInInitializerError(e);
            }
            initialized = true;
        }
    }

    /**
     * Normalize keys.
     * 
     * @param key
     *            The key.
     * @return The key normalized.
     */
    protected String normalizeKey(Object key) {
        return key == null ? null : String.valueOf(key).toLowerCase();
    }

    @Override
    public IMappingManager<T> bind(String name, T obj) {
        initialize();
        put(name, obj);
        return this;
    }

    @Override
    public Set<String> keySet() {
        initialize();
        return super.keySet();
    }

    @Override
    public T get(Object name) {
        if (name == null) {
            return null;
        }
        initialize();
        T c = super.get(normalizeKey(name));
        if (c != null) {
            c.initialize();
        }
        return c;
    }

    @Override
    public T getDefault() {
        return get(DEFAULT_NAME);
    }

    @Override
    public void setDefault(T obj) {
        bind(DEFAULT_NAME, obj);
    }
}
