/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import org.specrunner.context.IContext;
import org.specrunner.readers.IReader;
import org.specrunner.readers.ReaderException;
import org.specrunner.util.xom.UtilNode;
import org.specrunner.util.xom.node.INodeHolder;

import nu.xom.Node;

/**
 * Reader of content as XML.
 * 
 * @author Thiago Santos.
 * 
 */
@SuppressWarnings("serial")
public class ReaderXml implements IReader {

    @Override
    public void initialize() {
    }

    @Override
    public String read(IContext context, Object obj, Object[] args) throws ReaderException {
        if (obj instanceof Node) {
            return UtilNode.getChildrenAsString((Node) obj);
        }
        if (obj instanceof INodeHolder) {
            return UtilNode.getChildrenAsString(((INodeHolder) obj).getNode());
        }
        return String.valueOf(obj);
    }

    @Override
    public boolean isReplacer() {
        return true;
    }
}
