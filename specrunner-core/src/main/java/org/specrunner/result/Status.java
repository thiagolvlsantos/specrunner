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
package org.specrunner.result;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.util.IPresentation;

/**
 * A status.
 * 
 * @author Thiago Santos
 * 
 */
public class Status implements Comparable<Status>, IPresentation {

    /**
     * Ignored: is not error, has importance -1.
     */
    public static final Status IGNORED = new Status("ignored", false, -1);
    /**
     * Info: is not error, has importance 0.
     */
    public static final Status INFO = new Status("info", false, 0);
    /**
     * Warning: is not error, has importance 1.
     */
    public static final Status WARNING = new Status("warning", false, 1);
    /**
     * Success: is not error, has importance 2.
     */
    public static final Status SUCCESS = new Status("success", false, 2);
    /**
     * Failure: is error, has importance 3.
     */
    public static final Status FAILURE = new Status("failure", true, 3);

    protected String name;
    protected boolean error;
    protected double importance;

    protected Status(String name, boolean error, double importance) {
        this.name = name;
        this.error = error;
        this.importance = importance;
    }

    public String getName() {
        return name;
    }

    public String getCssName() {
        return "sr_" + getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    /**
     * Creates a Status with the given information.
     * 
     * @param name
     *            The name.
     * @param error
     *            The errors.
     * @param importance
     *            The importance.
     * @return The status.
     */
    public static Status newStatus(String name, boolean error, double importance) {
        return new Status(name, error, importance);
    }

    /**
     * Compares the status with another returning the most significant.
     * 
     * @param status
     *            The comparing status.
     * @return The resulting status.
     */
    public Status max(Status status) {
        return importance > status.importance ? this : status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

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
        Status other = (Status) obj;
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
    public int compareTo(Status o) {
        if (importance - o.importance > 0) {
            return -1;
        } else if (importance - o.importance < 0) {
            return 1;
        }
        return 0;
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