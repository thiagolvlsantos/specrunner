/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.util.cache.core;

import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheCleaner;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Abstract implementation of a factory.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractCacheFactory implements ICacheFactory {

    @Override
    public <K, T> ICache<K, T> newCache(String name) {
        return newCache(name, new ICacheCleaner<T>() {
            @Override
            public void destroy(T obj) {
                // do nothing.
            }
        });
    }

    @Override
    public <K, T> ICache<K, T> newCache(String name, ICacheCleaner<T> cleaner) {
        IFeatureManager fm = SRServices.getFeatureManager();
        ICache<K, T> cache = create(name, cleaner);
        fm.set(ICache.FEATURE_TIMEOUT, cache);
        fm.set(ICache.FEATURE_SIZE, cache);
        fm.set(ICache.FEATURE_CLEAN, cache);
        return cache;
    }

    /**
     * Create the cache object.
     * 
     * @param <K>
     *            Key type.
     * @param <T>
     *            Cache object type.
     * @param name
     *            Cache name.
     * @param cleaner
     *            A cache cleaner.
     * @return The cache instance.
     */
    protected abstract <K, T> ICache<K, T> create(String name, ICacheCleaner<T> cleaner);
}
