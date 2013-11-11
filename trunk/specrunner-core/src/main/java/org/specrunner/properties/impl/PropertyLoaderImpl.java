/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.properties.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.specrunner.SRServices;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.properties.PropertyLoaderException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;
import org.specrunner.util.resources.ResourceFinder;

/**
 * Default implementation of properties loading.
 * 
 * @author Thiago Santos
 * 
 */
public class PropertyLoaderImpl implements IPropertyLoader {

    /**
     * Cache of properties.
     */
    private static ICache<String, Properties> cache = SRServices.get(ICacheFactory.class).newCache(PropertyLoaderImpl.class.getName());

    @Override
    public Properties load(String file) throws PropertyLoaderException {
        synchronized (cache) {
            Properties result = cache.get(file);
            if (result != null) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Property reuse:" + result);
                }
                return result;
            }
            result = new Properties();
            List<URL> files;
            try {
                files = SRServices.get(ResourceFinder.class).getAllResources(file);
            } catch (IOException e) {
                throw new PropertyLoaderException(e);
            }
            loadUrls(files, result);
            cache.put(file, result);
            return result;
        }
    }

    /**
     * Load given URLs to a properties object.
     * 
     * @param files
     *            A list of file by URLs.
     * @param result
     *            The properties.
     * 
     * @throws PropertyLoaderException
     *             On loading erros.
     */
    protected void loadUrls(List<URL> files, Properties result) throws PropertyLoaderException {
        for (URL url : files) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("Loading properties:" + url);
            }
            InputStream in = null;
            try {
                in = url.openStream();
                if (in != null) {
                    result.load(in);
                } else {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Not found:" + url);
                    }
                }
            } catch (IOException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Not found:" + url, e);
                }
                throw new PropertyLoaderException(e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        if (UtilLog.LOG.isDebugEnabled()) {
                            UtilLog.LOG.debug(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }
}