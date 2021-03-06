/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.transformer.core;

import org.specrunner.source.ISource;
import org.specrunner.source.SourceException;
import org.specrunner.transformer.ITransformer;
import org.specrunner.transformer.ITransformerGroup;
import org.specrunner.util.composite.core.CompositeImpl;

/**
 * Default transformer group implementation.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class TransformerGroupImpl extends CompositeImpl<ITransformerGroup, ITransformer> implements ITransformerGroup {

    @Override
    public void initialize() {
        for (ITransformer t : getChildren()) {
            t.initialize();
        }
    }

    @Override
    public ISource transform(ISource source) throws SourceException {
        ISource tmp = source;
        for (ITransformer t : getChildren()) {
            tmp = t.transform(tmp);
        }
        return tmp;
    }
}
