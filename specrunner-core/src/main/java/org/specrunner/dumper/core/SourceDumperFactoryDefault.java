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
package org.specrunner.dumper.core;

/**
 * Default dumper implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceDumperFactoryDefault extends SourceDumperFactoryImpl {

    /**
     * Default constructor.
     */
    public SourceDumperFactoryDefault() {
        super(new SourceDumperGroupImpl().add(new SourceDumperResources()).add(new SourceDumperWritables()).add(new SourceDumperCenter()).add(new SourceDumperDetails()));
    }
}
