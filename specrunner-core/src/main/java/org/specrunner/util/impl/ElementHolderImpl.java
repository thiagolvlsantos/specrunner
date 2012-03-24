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

import nu.xom.Attribute;
import nu.xom.Element;

/**
 * Default implementation of element holder.
 * 
 * @author Thiago Santos
 * 
 */
public class ElementHolderImpl implements IElementHolder {

    /**
     * The element encapsulated.
     */
    private Element element;

    /**
     * Create a element holder.
     * 
     * @param element
     *            The element.
     */
    public ElementHolderImpl(Element element) {
        this.element = element;
    }

    @Override
    public Element getElement() {
        return element;
    }

    @Override
    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public String getValue() {
        return element.getValue();
    }

    @Override
    public boolean hasAttribute(String name) {
        return element.getAttribute(name) != null;
    }

    @Override
    public String getAttribute(String name) {
        return hasAttribute(name) ? element.getAttribute(name).getValue() : null;
    }

    @Override
    public void setAttribute(String name, String value) {
        if (hasAttribute(name)) {
            element.getAttribute(name).setValue(value);
        } else {
            element.addAttribute(new Attribute(name, value));
        }
    }

    @Override
    public void removeAttribute(String name) {
        for (int i = 0; i < element.getAttributeCount(); i++) {
            Attribute att = element.getAttribute(i);
            if (att.getQualifiedName().equalsIgnoreCase(name)) {
                element.removeAttribute(att);
                break;
            }
        }
    }

    @Override
    public String toString() {
        return getElement() != null ? getElement().toXML() : "null";
    }
}
