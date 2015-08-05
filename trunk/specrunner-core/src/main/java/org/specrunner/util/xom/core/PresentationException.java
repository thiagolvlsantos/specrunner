/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.util.xom.core;

import nu.xom.Node;

import org.specrunner.util.xom.IPresentation;

/**
 * Generic presentation wrapper of exceptions.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class PresentationException extends Exception implements IPresentation {

    /**
     * The presentation object.
     */
    private IPresentation presentation;

    /**
     * Set a presentation.
     * 
     * @param presentation
     *            A presentation object.
     */
    public PresentationException(IPresentation presentation) {
        this.presentation = presentation;
    }

    @Override
    public String asString() {
        return presentation.asString();
    }

    @Override
    public Node asNode() {
        return presentation.asNode();
    }

    @Override
    public String getMessage() {
        return asString();
    }
}
