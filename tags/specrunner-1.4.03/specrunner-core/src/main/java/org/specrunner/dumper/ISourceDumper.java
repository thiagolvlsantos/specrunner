/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.dumper;

import java.util.Map;

import org.specrunner.result.IResultSet;
import org.specrunner.source.IEncoded;
import org.specrunner.source.ISource;

/**
 * Dumps source and results to somewhere.
 * 
 * @author Thiago Santos
 * 
 */
public interface ISourceDumper extends IEncoded {

    /**
     * Extra information to be dumped.
     * 
     * @param source
     *            The specification input.
     * @param result
     *            The result.
     * @param model
     *            Extra information model.
     * @throws SourceDumperException
     *             On dumping error.
     */
    void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException;
}