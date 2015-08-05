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
package org.specrunner.properties.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
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
    protected static ICache<String, List<Properties>> cache = SRServices.get(ICacheFactory.class).newCache(PropertyLoaderImpl.class.getName());

    @Override
    public List<Properties> load(String file) throws PropertyLoaderException {
        synchronized (cache) {
            List<Properties> result = cache.get(file);
            if (result != null) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Property reuse:" + result);
                }
                return result;
            }
            List<URL> files;
            try {
                files = SRServices.get(ResourceFinder.class).initilize().getAllResources(file);
            } catch (IOException e) {
                throw new PropertyLoaderException(e);
            }
            result = loadUrls(files);
            sort(result);
            int index = 0;
            for (Properties p : result) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Property order (" + (index++) + ")=" + p);
                }
                p.remove("index");
            }
            cache.put(file, result);
            return result;
        }
    }

    /**
     * Load given URLs to a properties object.
     * 
     * @param files
     *            A list of file by URLs.
     * @return The properties list.
     * 
     * @throws PropertyLoaderException
     *             On loading erros.
     */
    protected List<Properties> loadUrls(List<URL> files) throws PropertyLoaderException {
        List<Properties> properties = new LinkedList<Properties>();
        for (URL url : files) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Loading properties:" + url);
            }
            InputStream in = null;
            try {
                in = url.openStream();
                if (in != null) {
                    Properties p = new Properties();
                    p.load(in);
                    properties.add(p);
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
        return properties;
    }

    /**
     * Sort property files using attribute index to order.
     * 
     * @param props
     *            The property files list.
     */
    protected void sort(List<Properties> props) {
        Collections.sort(props, new Comparator<Properties>() {
            @Override
            public int compare(Properties o1, Properties o2) {
                String key = "index";
                double index1 = o1.containsKey(key) ? Double.valueOf((String) o1.get(key)) : 0.0;
                double index2 = o2.containsKey(key) ? Double.valueOf((String) o2.get(key)) : 0.0;
                return index1 < index2 ? -1 : (index2 < index1 ? 1 : 0);
            }
        });
    }
}
