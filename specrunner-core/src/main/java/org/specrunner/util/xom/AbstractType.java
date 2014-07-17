/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.util.xom;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;


/**
 * A type with importance.
 * 
 * @param <T>
 *            The type.
 * @author Thiago Santos
 * 
 */
public class AbstractType<T extends AbstractType<T>> implements Comparable<T>, IPresentation {

    /**
     * The type name.
     */
    protected String name;
    /**
     * Its priority, for example, ASSERTION has higher priority than ACTION,
     * which has higher priority than UNDEF.
     */
    protected double importance;

    /**
     * Creates a type.
     * 
     * @param name
     *            The name.
     * @param importance
     *            The importance.
     */
    protected AbstractType(String name, double importance) {
        this.name = name;
        this.importance = importance;
    }

    /**
     * The type name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * The CSS which represents the type.
     * 
     * @return The CSS name.
     */
    public String getCssName() {
        return "sr_" + getName();
    }

    /**
     * Set the name.
     * 
     * @param name
     *            The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        T other = (T) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(T o) {
        if (importance - o.importance > 0) {
            return -1;
        } else if (importance - o.importance < 0) {
            return 1;
        }
        return 0;
    }

    /**
     * Compares the status with another returning the most significant.
     * 
     * @param obj
     *            The comparing status.
     * @return The resulting status.
     */
    @SuppressWarnings("unchecked")
    public T max(T obj) {
        return importance > obj.importance ? (T) this : obj;
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    public Node asNode() {
        Element result = new Element("span");
        result.addAttribute(new Attribute("class", getCssName()));
        result.appendChild(getName());
        return result;
    }
}