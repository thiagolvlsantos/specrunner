/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.source.resource;

import java.util.List;

import org.specrunner.source.ISource;
import org.specrunner.source.resource.positional.Position;

/**
 * Generic resource manager.
 * 
 * @author Thiago Santos.
 * 
 */
public interface IResourceManager extends List<IResource> {

    /**
     * The resource manager parent source.
     * 
     * @return The parent.
     */
    ISource getParent();

    /**
     * Add default CSS files. By default 'specrunner.css' and
     * 'specrunner_debug.css' are added, the former always, and the remainder
     * when debug log is enabled.
     * 
     * @throws ResourceException
     *             On add errors.
     */
    void addDefaultCss() throws ResourceException;

    /**
     * Add default JavaScript files. By default 'specrunner.js' and 'jquery.js'
     * are added.
     * 
     * @throws ResourceException
     *             On add errors.
     */
    void addDefaultJs() throws ResourceException;

    /**
     * Adds a CSS to the head end.
     * 
     * @param path
     *            The reference to the resource.
     * @param classpath
     *            true, to find in classpath, false, to find in file system.
     * @param ref
     *            The resource type.
     * @return The source after add changes are reflected.
     * @throws ResourceException
     *             On add errors.
     */
    IResource addCss(String path, boolean classpath, EType ref) throws ResourceException;

    /**
     * Adds a CSS specified by path to a given position.
     * 
     * @param path
     *            The reference to the resource.
     * @param classpath
     *            true, to find in classpath, false, to find in file system.
     * @param ref
     *            The resource type.
     * @param position
     *            The position to place the resource in the source reference.
     * @return The source after add changes are reflected.
     * @throws ResourceException
     *             On add errors.
     */
    IResource addCss(String path, boolean classpath, EType ref, Position position) throws ResourceException;

    /**
     * Adds a JS specified by path to a given position.
     * 
     * @param path
     *            The reference to the resource.
     * @param classpath
     *            true, to find in classpath, false, to find in file system.
     * @param ref
     *            The resource type.
     * @return The source after add changes are reflected.
     * @throws ResourceException
     *             On add errors.
     */
    IResource addJs(String path, boolean classpath, EType ref) throws ResourceException;

    /**
     * Adds a JS specified by path to a given position.
     * 
     * @param path
     *            The reference to the resource.
     * @param classpath
     *            true, to find in classpath, false, to find in file system.
     * @param ref
     *            The resource type.
     * @param position
     *            The position to place the resource in the source reference.
     * @return The source after add changes are reflected.
     * @throws ResourceException
     *             On add errors.
     */
    IResource addJs(String path, boolean classpath, EType ref, Position position) throws ResourceException;

    /**
     * Import resources from other manager.
     * 
     * @param resources
     *            Resources to the copied.
     */
    void merge(IResourceManager resources);
}