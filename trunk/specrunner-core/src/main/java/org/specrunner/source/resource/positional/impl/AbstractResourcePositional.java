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
package org.specrunner.source.resource.positional.impl;

import org.specrunner.source.ISource;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.impl.AbstractResource;
import org.specrunner.source.resource.positional.IResourcePositional;
import org.specrunner.source.resource.positional.Position;

/**
 * A positional resource.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractResourcePositional extends AbstractResource implements IResourcePositional {
    /**
     * The resource position inside a source.
     */
    private Position position;

    /**
     * Creates a positional resource.
     * 
     * @param parent
     *            The source parent.
     * @param path
     *            The resource path.
     * @param classpath
     *            The classpath flag.
     * @param type
     *            The resource nature.
     * @param position
     *            The resource position.
     */
    protected AbstractResourcePositional(ISource parent, String path, boolean classpath, EType type, Position position) {
        super(parent, path, classpath, type);
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }
}