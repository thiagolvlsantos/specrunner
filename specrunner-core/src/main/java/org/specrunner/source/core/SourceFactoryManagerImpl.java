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
package org.specrunner.source.core;

import java.util.Set;

import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.source.SourceException;
import org.specrunner.util.mapping.core.MappingManagerImpl;

/**
 * Default implementation for a source manager. Get the source factory based on
 * file extension type.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class SourceFactoryManagerImpl extends MappingManagerImpl<ISourceFactory> implements ISourceFactoryManager {

    /**
     * Default constructor.
     */
    public SourceFactoryManagerImpl() {
        super("sr_sources.properties");
    }

    @Override
    public Set<String> keySet() {
        initialize();
        return super.keySet();
    }

    @Override
    public ISource newSource(Object source) throws SourceException {
        initialize();
        if (source != null) {
            String name = String.valueOf(source);
            int pos = name.lastIndexOf('.');
            if (pos >= 0) {
                ISourceFactory sf = get(name.substring(pos + 1).toLowerCase().trim());
                if (sf != null) {
                    return sf.newSource(source);
                }
            }
        }
        throw new SourceException("Source reader for '" + source + "' not found.");
    }
}
