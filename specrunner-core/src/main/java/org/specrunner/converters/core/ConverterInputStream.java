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
package org.specrunner.converters.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.specrunner.converters.ConverterException;
import org.specrunner.util.UtilIO;

/**
 * Convert URI references to InputStreams.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ConverterInputStream extends ConverterDefault {

    @Override
    public Object convert(Object obj, Object[] args) throws ConverterException {
        if (obj == null) {
            return null;
        }
        try {
            return UtilIO.getStream((new URI(String.valueOf(obj))));
        } catch (IOException e) {
            throw new ConverterException(e);
        } catch (URISyntaxException e) {
            throw new ConverterException(e);
        }
    }
}
