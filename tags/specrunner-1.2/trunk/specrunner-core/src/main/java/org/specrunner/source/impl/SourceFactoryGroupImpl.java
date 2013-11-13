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

import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.ISourceFactoryGroup;
import org.specrunner.source.SourceException;
import org.specrunner.util.composite.CompositeImpl;

/**
 * Default source factory group implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceFactoryGroupImpl extends CompositeImpl<ISourceFactoryGroup, ISourceFactory> implements ISourceFactoryGroup {

    @Override
    public boolean accept(Object source) {
        boolean result = false;
        for (ISourceFactory sf : getChildren()) {
            if (sf.accept(source)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public ISource newSource(Object source) throws SourceException {
        for (ISourceFactory sf : getChildren()) {
            if (sf.accept(source)) {
                return sf.newSource(source);
            }
        }
        return null;
    }
}