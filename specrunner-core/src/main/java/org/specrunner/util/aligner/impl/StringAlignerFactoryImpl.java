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
package org.specrunner.util.aligner.impl;

import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;

/**
 * Default alignment factory implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class StringAlignerFactoryImpl implements IStringAlignerFactory {

    @Override
    public IStringAligner align(String expected, String received) {
        return new NeedlemanWunschAligner(expected, received);
    }

    @Override
    public IStringAligner align(String expected, String received, char fillCharacter) {
        return new NeedlemanWunschAligner(expected, received, fillCharacter);
    }
}