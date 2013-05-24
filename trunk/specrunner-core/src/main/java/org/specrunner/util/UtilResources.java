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
package org.specrunner.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.source.resource.positional.IResourcePositional;

/**
 * Resources utilities.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilResources {

    /**
     * Hidden constructor.
     */
    private UtilResources() {
    }

    /**
     * Add a positional resource to a document.
     * 
     * @param source
     *            The source.
     * @param resourcePositional
     *            The positional resource.
     * @throws ResourceException
     *             The adding errors.
     */
    public static void addToDoc(ISource source, IResourcePositional resourcePositional) throws ResourceException {
        try {
            Document d = source.getDocument();
            Nodes ns = d.query(resourcePositional.getPosition().getXpath());
            if (ns.size() > 0) {
                Node n = ns.get(0);
                ParentNode p = n.getParent();
                Node a = resourcePositional.asNode();
                switch (resourcePositional.getPosition().getPlace()) {
                case BEFORE:
                    p.insertChild(a, p.indexOf(n));
                    break;
                case AFTER:
                    p.insertChild(a, p.indexOf(n) + 1);
                    break;
                case START:
                    if (n instanceof ParentNode) {
                        ((ParentNode) n).insertChild(a, 0);
                    }
                    break;
                case END:
                    if (n instanceof ParentNode) {
                        ((ParentNode) n).appendChild(a);
                    }
                    break;
                default:
                }
            }
        } catch (SourceException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * Get the most specific file. The one which is closer in classpath lookup.
     * 
     * @param file
     *            The file.
     * @return The resource URL.
     * @throws IOException
     *             On lookup errors.
     */
    public static URL getMostSpecific(String file) throws IOException {
        List<URL> files = getFileList(file);
        if (files.isEmpty()) {
            return null;
        }
        return files.get(files.size() - 1);
    }

    /**
     * Get the list of all files matching the given name.
     * 
     * @param file
     *            The file name.
     * @return The file list.
     * @throws IOException
     *             On lookup errors.
     */
    public static List<URL> getFileList(String file) throws IOException {
        List<URL> files = new LinkedList<URL>();
        String standard = getDefaultName(file);
        files.addAll(find(standard));
        files.addAll(find(file));
        files = sort(files);
        return files;
    }

    /**
     * Get default name for resource files.
     * 
     * @param file
     *            The file name.
     * @return The corresponding default file name.
     */
    public static String getDefaultName(String file) {
        int pos = file.lastIndexOf('.');
        return file.substring(0, pos) + "_default" + file.substring(pos);
    }

    /**
     * Find a given file in classloader.
     * 
     * @param file
     *            The file name.
     * @return The list of files satisfying the name restriction in classpath.
     * @throws IOException
     *             On loading list errors.
     */
    public static List<URL> find(String file) throws IOException {
        List<URL> result = new LinkedList<URL>();
        Enumeration<URL> urls = ClassLoader.getSystemResources(file);
        while (urls.hasMoreElements()) {
            result.add(urls.nextElement());
        }
        return result;
    }

    /**
     * Sort the files using reverse order.
     * 
     * @param files
     *            The files.
     * @return The list in reverse order.
     */
    public static List<URL> sort(List<URL> files) {
        Collections.reverse(files);
        return files;
    }
}