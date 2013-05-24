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
package org.specrunner.source;

/**
 * A factory of sources.
 * 
 * @author Thiago Santos
 * 
 */
public interface ISourceFactory {

    /**
     * Name of feature to set reader encoding.
     */
    String FEATURE_ENCODING = ISourceFactory.class + ".encoding";

    /**
     * Default encoding.
     */
    String DEFAULT_ENCODING = "ISO-8859-1";

    /**
     * Check if this factory accept a given source.
     * 
     * @param source
     *            The source.
     * @return true, if accept, false, otherwise.
     */
    boolean accept(Object source);

    /**
     * Creates a source from a String.
     * 
     * @param source
     *            The source of specification.
     * @return The source.
     * @throws SourceException
     *             On creation errors.
     */
    ISource newSource(Object source) throws SourceException;
}
