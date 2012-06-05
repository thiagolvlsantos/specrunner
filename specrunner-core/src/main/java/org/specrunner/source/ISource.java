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
package org.specrunner.source;

import java.io.File;
import java.net.URI;

import nu.xom.Document;

import org.specrunner.source.resource.IResourceManager;

/**
 * Stand for the source of specifications.
 * 
 * @author Thiago Santos
 * 
 */
public interface ISource {

    /**
     * The source reference as String.
     * 
     * @return The source as String.
     */
    String getString();

    /**
     * The source reference as a <code>java.io.File</code> instance.
     * 
     * @return A file, if possible, otherwise null, if a File representation do
     *         not exist.
     */
    File getFile();

    /**
     * The source reference as a <code>java.net.URI</code> instance.
     * 
     * @return A uri, if possible, otherwise null, if a URI representation do
     *         not exist.
     */
    URI getURI();

    /**
     * Returns the reference as a relative String.
     * 
     * @param other
     *            The reference to be compared.
     * @return The relative reference as String.
     */
    String relative(ISource other);

    /**
     * The relative source resolved.
     * 
     * @param other
     *            The reference to be opened.
     * @return The relative reference.
     * @throws SourceException
     *             On creation errors.
     */
    ISource resolve(ISource other) throws SourceException;

    /**
     * The source factory.
     * 
     * @return The source factory used to create the source.
     */
    ISourceFactory getFactory();

    /**
     * A resource manager. Responsible for handling JavaScripts, CSS, etc,
     * related to the specification.
     * 
     * @return The resource reference.
     */
    IResourceManager getManager();

    /**
     * The specification document.
     * 
     * @return The specification as document.
     * @throws SourceException
     *             On creation error.
     */
    Document getDocument() throws SourceException;
}