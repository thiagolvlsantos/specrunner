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
package org.specrunner.source.impl;

import java.io.IOException;
import java.io.InputStream;

/**
 * Read a feature from a input stream.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFeatureReader {

    /**
     * Read feature from load.
     * 
     * @param in
     *            The input stream.
     * @param encoding
     *            The encoding.
     * @return The feature.
     * @throws IOException
     *             On reading errors.
     */
    Feature load(InputStream in, String encoding) throws IOException;
}
