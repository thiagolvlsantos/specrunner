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
package org.specrunner.pipeline;


//CHECKSTYLE:OFF
@SuppressWarnings("serial")
public class PipelineException extends Exception {

    public PipelineException() {
    }

    public PipelineException(String message) {
        super(message);
    }

    public PipelineException(Throwable cause) {
        super(cause);
    }

    public PipelineException(String message, Throwable cause) {
        super(message, cause);
    }
}
// CHECKSTYLE:ON
