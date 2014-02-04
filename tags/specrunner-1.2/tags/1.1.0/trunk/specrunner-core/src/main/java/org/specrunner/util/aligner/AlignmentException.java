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
package org.specrunner.util.aligner;

import org.specrunner.util.IPresentation;

//CHECKSTYLE:OFF
@SuppressWarnings("serial")
public abstract class AlignmentException extends Exception implements IPresentation {

    protected IStringAligner aligner;

    public AlignmentException(IStringAligner aligner) {
        this.aligner = aligner;
    }

    public IStringAligner getAligner() {
        return aligner;
    }

    @Override
    public String getMessage() {
        return "Strings are different:\n(expected        )" + getAligner().getExpected() + "\n(received        )" + getAligner().getReceived() + "\n(expected aligned)" + aligner.getExpectedAligned() + "\n(received aligned)" + aligner.getReceivedAligned();
    }
}
// CHECKSTYLE:ON