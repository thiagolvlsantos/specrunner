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
package org.specrunner.util.output.impl;

import java.io.IOException;

import org.specrunner.util.output.IOutput;

/**
 * Basic output target.
 * 
 * @author Thiago Santos
 * 
 */
public class OutputSysout implements IOutput {

    @Override
    public void print(Object obj) {
        System.out.print(obj);
    }

    @Override
    public void printf(Object obj, Object... args) {
        System.out.printf(String.valueOf(obj), args);
    }

    @Override
    public void println(Object obj) {
        System.out.println(obj);
    }

    @Override
    public void close() throws IOException {
        // nothing.
    }
}
