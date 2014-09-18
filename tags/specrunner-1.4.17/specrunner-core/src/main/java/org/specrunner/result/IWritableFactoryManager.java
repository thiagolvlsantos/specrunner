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
package org.specrunner.result;

import org.specrunner.util.mapping.IMappingManager;

/**
 * A writable factory.
 * 
 * @author Thiago Santos
 * 
 */
public interface IWritableFactoryManager extends IMappingManager<IWritableFactory<?>> {

    /**
     * Gets a writable factory by type.
     * 
     * @param <T>
     *            The object source type.
     * @param type
     *            The type of object to be used for writable creation.
     * @return A factory, if it exists, null, otherwise.
     */
    <T> IWritableFactory<T> get(Class<T> type);
}
