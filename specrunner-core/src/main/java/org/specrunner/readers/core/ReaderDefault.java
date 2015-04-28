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
package org.specrunner.readers.core;

import nu.xom.Node;

import org.specrunner.readers.IReader;
import org.specrunner.readers.ReaderException;
import org.specrunner.util.xom.node.INodeHolder;

/**
 * Reader default.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ReaderDefault implements IReader {

    @Override
    public void initialize() {
    }

    @Override
    public String read(Object obj, Object[] args) throws ReaderException {
        if (obj instanceof Node) {
            return ((Node) obj).getValue();
        }
        if (obj instanceof INodeHolder) {
            return ((INodeHolder) obj).getValue();
        }
        return String.valueOf(obj);
    }
}