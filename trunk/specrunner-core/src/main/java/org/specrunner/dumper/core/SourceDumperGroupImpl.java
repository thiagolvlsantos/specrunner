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
package org.specrunner.dumper.core;

import java.util.Map;

import org.specrunner.dumper.ISourceDumper;
import org.specrunner.dumper.ISourceDumperGroup;
import org.specrunner.dumper.SourceDumperException;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.source.core.UtilEncoding;
import org.specrunner.util.composite.core.CompositeImpl;

/**
 * Default implementation of a source dumper group.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperGroupImpl extends CompositeImpl<ISourceDumperGroup, ISourceDumper> implements ISourceDumperGroup {

    @Override
    public String getEncoding() {
        return UtilEncoding.getEncoding();
    }

    @Override
    public void dump(ISource source, IResultSet result, Map<String, Object> model) throws SourceDumperException {
        for (ISourceDumper d : getChildren()) {
            d.dump(source, result, model);
        }
    }
}