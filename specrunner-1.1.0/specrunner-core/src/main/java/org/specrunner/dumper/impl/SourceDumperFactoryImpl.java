/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.dumper.impl;

import org.specrunner.dumper.ISourceDumper;
import org.specrunner.dumper.ISourceDumperFactory;
import org.specrunner.dumper.SourceDumperException;

/**
 * Default dumper implementation.
 * 
 * @author Thiago
 * 
 */
public class SourceDumperFactoryImpl implements ISourceDumperFactory {

    /**
     * The reusable dumper.
     */
    protected ISourceDumper dumper;

    /**
     * Creates a factory of reusable dumpers.
     * 
     * @param dumper
     *            The dumper.
     */
    public SourceDumperFactoryImpl(ISourceDumper dumper) {
        setDumper(dumper);
    }

    /**
     * Gets the dumper.
     * 
     * @return The dumper.
     */
    public ISourceDumper getDumper() {
        return dumper;
    }

    /**
     * Sets the runner.
     * 
     * @param dumper
     *            The dumper.
     */
    public void setDumper(ISourceDumper dumper) {
        this.dumper = dumper;
    }

    @Override
    public ISourceDumper newDumper() throws SourceDumperException {
        return getDumper();
    }
}