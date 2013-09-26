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
package org.specrunner.source.impl;

import java.util.LinkedList;
import java.util.List;

public abstract class Description {

    protected String name;
    protected List<String> description = new LinkedList<String>();

    public Description(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Description setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getDescription() {
        return description;
    }

    public Description setDescription(List<String> description) {
        this.description = description;
        return this;
    }

    public Description addDescription(String description) {
        this.description.add(description);
        return this;
    }
}