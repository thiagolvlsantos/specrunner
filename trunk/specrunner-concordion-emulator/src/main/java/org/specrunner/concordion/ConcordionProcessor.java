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
package org.specrunner.concordion;

import org.specrunner.source.namespace.core.AbstractNamespaceProcessor;

/**
 * Basic Concordion processor.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class ConcordionProcessor extends AbstractNamespaceProcessor {

    /**
     * Default constructor of Concordion analyzers.
     * 
     * @param tag
     *            A tag.
     */
    protected ConcordionProcessor(String tag) {
        super(null, "http://www.concordion.org/2007/concordion", tag);
    }

    /**
     * Prepare variable.
     * 
     * @param value
     *            The value.
     * @return The prepared value.
     */
    protected String cleanVar(String value) {
        if (value == null) {
            return null;
        }
        // replace meta-variables
        String tmp = replaceMeta(value);
        // replace variables names
        tmp = tmp.replace("#", "");
        // remove spaces
        return tmp.trim();
    }

    /**
     * Replace meta-variables.
     * 
     * @param value
     *            The value to replace.
     * @return The replaced variable.
     */
    protected String replaceMeta(String value) {
        return value.replace("#TEXT", "$TEXT").replace("#HREF", "href");
    }
}
