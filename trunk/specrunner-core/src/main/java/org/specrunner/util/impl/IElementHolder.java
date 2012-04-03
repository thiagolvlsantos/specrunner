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
package org.specrunner.util.impl;

import nu.xom.Element;

/**
 * Stands for any object which hold an element.
 * 
 * @author Thiago Santos
 * 
 */
public interface IElementHolder {

    /**
     * Get the hold element.
     * 
     * @return The element.
     */
    Element getElement();

    /**
     * Set the hold element.
     * 
     * @param element
     *            The element.
     */
    void setElement(Element element);

    /**
     * Get element value.
     * 
     * @return Element value.
     */
    String getValue();

    /**
     * Check if an given attribute is present.
     * 
     * @param name
     *            The attribute name.
     * @return true, if present, false, otherwise.
     */
    boolean hasAttribute(String name);

    /**
     * Get attribute value by type.
     * 
     * @param name
     *            The name.
     * @return The value, if exists, null, otherwise.
     */
    String getAttribute(String name);

    /**
     * Set attribute value.
     * 
     * @param name
     *            The name.
     * @param value
     *            The value.
     */
    void setAttribute(String name, String value);

    /**
     * Remove a attribute.
     * 
     * @param name
     *            The attribute name.
     */
    void removeAttribute(String name);
}
