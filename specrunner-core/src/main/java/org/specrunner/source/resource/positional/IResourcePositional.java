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
package org.specrunner.source.resource.positional;

import org.specrunner.source.resource.IResource;

/**
 * Definition of a resource. A resource is something which is part of the
 * specification document, i.e. a CSS file, a image, etc.
 * 
 * @author Thiago Santos
 * 
 */
public interface IResourcePositional extends IResource {

    /**
     * The position to add the resource.
     * 
     * @return The resource position.
     */
    Position getPosition();

    /**
     * Set the position.
     * 
     * @param position
     *            The resource position.
     */
    void setPosition(Position position);
}
