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
package org.specrunner.util.resources;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.SpecRunnerServices;
import org.specrunner.properties.impl.PropertyLoaderImpl;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Resources utilities.
 * 
 * @author Thiago Santos
 * 
 */
public class ResourceFinder {

    /**
     * Cache of resources.
     */
    private static ICache<String, List<URL>> cache = SpecRunnerServices.get(ICacheFactory.class).newCache(PropertyLoaderImpl.class.getName());

    /**
     * Get default name for resource files.
     * 
     * @param resource
     *            The resource name.
     * @return The corresponding default resource name.
     */
    public String getDefault(String resource) {
        int pos = resource.lastIndexOf('.');
        return resource.substring(0, pos) + "_default" + resource.substring(pos);
    }

    /**
     * Get the most specific file. The one which is closer in classpath lookup.
     * 
     * @param resource
     *            The resource.
     * @return The resource URL.
     * @throws IOException
     *             On lookup errors.
     */
    public URL getSpecific(String resource) throws IOException {
        List<URL> files = getAllResources(resource);
        if (files.isEmpty()) {
            return null;
        }
        return files.get(files.size() - 1);
    }

    /**
     * List of all resource URLs associated to a given file, including its
     * suffix '_default' corresponding version.
     * 
     * @param resource
     *            The resource file.
     * @return The resource URL list.
     * @throws IOException
     *             On lookup error.
     */
    public List<URL> getAllResources(String resource) throws IOException {
        List<URL> files = cache.get(resource);
        if (files != null) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Resource reused '" + resource + "' :" + files);
            }
            return files;
        }
        files = new LinkedList<URL>();
        String standard = getDefault(resource);
        files.addAll(getResources(standard));
        files.addAll(getResources(resource));
        if (UtilLog.LOG.isDebugEnabled()) {
            log("Resource list:", files);
        }
        files = filter(files);
        if (UtilLog.LOG.isDebugEnabled()) {
            log("Resource list filtered:", files);
        }
        files = sort(files);
        if (UtilLog.LOG.isDebugEnabled()) {
            log("Resource list sorted:", files);
        }
        cache.put(resource, files);
        return files;
    }

    /**
     * Print load information.
     * 
     * @param msg
     *            A message.
     * @param files
     *            The resource list.
     */
    protected void log(String msg, List<URL> files) {
        UtilLog.LOG.debug(msg);
        for (URL url : files) {
            UtilLog.LOG.debug("\t" + url);
        }
    }

    /**
     * Find a given file in classloader.
     * 
     * @param resource
     *            The resource name.
     * @return The list of files satisfying the name restriction in classpath.
     * @throws IOException
     *             On loading list errors.
     */
    public List<URL> getResources(String resource) throws IOException {
        List<URL> result = new LinkedList<URL>();
        Enumeration<URL> urls = ClassLoader.getSystemResources(resource);
        while (urls.hasMoreElements()) {
            result.add(urls.nextElement());
        }
        return result;
    }

    /**
     * Filter resources.
     * 
     * @param resources
     *            The resources.
     * @return The list filtered resources. Default implementation filters
     *         nothing.
     */
    public List<URL> filter(List<URL> resources) {
        return resources;
    }

    /**
     * Sort the resources using reverse order.
     * 
     * @param resources
     *            The resources.
     * @return The sorted list. Default implementation sources is in reverse
     *         order.
     */
    public List<URL> sort(List<URL> resources) {
        Collections.reverse(resources);
        return resources;
    }
}