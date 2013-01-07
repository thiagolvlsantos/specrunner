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

import org.specrunner.source.ISource;
import org.specrunner.util.xom.IPresentation;

/**
 * A resource is something attached somehow to the source. For example, CSSs,
 * Java Scripts, images, etc.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResource extends IPresentation {

    /**
     * The reference parent.
     * 
     * @return The parent.
     */
    ISource getParent();

    /**
     * Change the resource parent.
     * 
     * @param parent
     *            The new parent.
     */
    void setParent(ISource parent);

    /**
     * The resource path, relative to classpath. i.e. '/css/specrunner.css'
     * 
     * @return The path.
     */
    String getResourcePath();

    /**
     * Set the resource path.
     * 
     * @param path
     *            The resource path.
     */
    void setResourcePath(String path);

    /**
     * The resource is relative to classpath?
     * 
     * @return true, if so, false, otherwise.
     */
    boolean isClasspath();

    /**
     * Set the classpath context.
     * 
     * @param classpath
     *            true, for classpath relative, false, otherwise.
     */
    void setClasspath(boolean classpath);

    /**
     * The resource type.
     * 
     * @return The resource.
     */
    EType getType();

    /**
     * Resource type.
     * 
     * @param type
     *            The new type.
     */
    void setType(EType type);

    /**
     * Write the resource to the target, e returns the resulting source.
     * 
     * @param target
     *            The target reference.
     * @return The result source.
     * @throws ResourceException
     *             On writing errors.
     */
    ISource writeTo(ISource target) throws ResourceException;
}