/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.properties.core.PropertyLoaderImpl;
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
     * Comparator feature.
     */
    public static final String FEATURE_COMPARATOR = ResourceFinder.class.getName() + ".comparator";

    /**
     * Cache of resources.
     */
    protected static ICache<String, List<URL>> cache = SRServices.get(ICacheFactory.class).newCache(PropertyLoaderImpl.class.getName());

    /**
     * Comparator element.
     */
    private Comparator<URL> comparator;

    /**
     * Get the resources comparator.
     * 
     * @return The comparator.
     */
    public Comparator<URL> getComparator() {
        return comparator;
    }

    /**
     * Set the comparator.
     * 
     * @param comparator
     *            Comparator.
     */
    public void setComparator(Comparator<URL> comparator) {
        this.comparator = comparator;
    }

    /**
     * Get default name for resource files.
     * 
     * @param resource
     *            The resource name.
     * @return The corresponding default resource name.
     */
    public List<String> getDefault(String resource) {
        int pos = resource.lastIndexOf('.');
        String default_short = resource.substring(0, pos) + "_df" + resource.substring(pos);
        String default_long = resource.substring(0, pos) + "_default" + resource.substring(pos);
        return Arrays.asList(default_short, default_long);
    }

    /**
     * Initialize comparator, if required.
     * 
     * @return Finder itself.
     */
    public ResourceFinder initilize() {
        // update comparator
        comparator = null;
        SRServices.getFeatureManager().set(FEATURE_COMPARATOR, this);
        return this;
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
        synchronized (cache) {
            List<URL> files = cache.get(resource);
            if (files != null) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Resource reused '" + resource + "' :" + files);
                }
                // get a copy, filtered and sorted. TODO: if the filter is
                // changes the files here can be only a subset of expected ones.
                return filter(sort(new LinkedList<URL>(files)));
            }
            files = new LinkedList<URL>();
            List<String> standard = getDefault(resource);
            files.addAll(getResources(standard.toArray(new String[0])));
            files.addAll(getResources(resource));
            if (UtilLog.LOG.isTraceEnabled()) {
                log("Resource list:", files);
            }
            files = filter(files);
            if (UtilLog.LOG.isTraceEnabled()) {
                log("Resource list filtered:", files);
            }
            files = sort(files);
            if (UtilLog.LOG.isTraceEnabled()) {
                log("Resource list sorted:", files);
            }
            cache.put(resource, files);
            return files;
        }
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
        UtilLog.LOG.trace(msg);
        for (URL url : files) {
            UtilLog.LOG.trace("\t" + url);
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
    public List<URL> getResources(String... resources) throws IOException {
        List<URL> result = new LinkedList<URL>();
        if (resources != null) {
            for (String resource : resources) {
                Enumeration<URL> urls = ClassLoader.getSystemResources(resource);
                while (urls.hasMoreElements()) {
                    result.add(urls.nextElement());
                }
            }
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
        if (comparator == null) {
            Collections.reverse(resources);
        } else {
            Collections.sort(resources, comparator);
        }
        return resources;
    }
}
