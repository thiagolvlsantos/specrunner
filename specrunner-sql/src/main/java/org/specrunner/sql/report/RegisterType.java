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
package org.specrunner.sql.report;

import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.util.xom.IPresentation;

/**
 * The register types.
 * 
 * @author Thiago Santos
 * 
 */
public enum RegisterType implements IPresentation {
    /**
     * Indicates a missing register.
     */
    MISSING("missing", "missing"),
    /**
     * Indicates an unexpected register.
     */
    EXTRA("extra", "extra"),
    /**
     * Registers match, but types there are some different columns.
     */
    DIFFERENT("not equals", "different");

    /**
     * Type simbol.
     */
    private String simbol;
    /**
     * Type alias.
     */
    private String code;

    /**
     * Constructor.
     * 
     * @param simbol
     *            Type simbol.
     * @param code
     *            The text code.
     */
    private RegisterType(String simbol, String code) {
        this.simbol = simbol;
        this.code = code;
    }

    /**
     * Gets the style of this type.
     * 
     * @return The style.
     */
    public String getStyle() {
        return "sr_" + code.toLowerCase();
    }

    @Override
    public String asString() {
        return simbol;
    }

    @Override
    public Node asNode() {
        return new Text(asString());
    }
}
