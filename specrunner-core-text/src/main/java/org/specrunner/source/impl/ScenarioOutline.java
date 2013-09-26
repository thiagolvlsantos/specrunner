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

public class ScenarioOutline extends Scenario {

    private List<String> names = new LinkedList<String>();
    private List<List<String>> examples = new LinkedList<List<String>>();

    public ScenarioOutline(String name) {
        super(name);
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public ScenarioOutline add(String name) {
        names.add(name);
        return this;
    }

    public List<List<String>> getExamples() {
        return examples;
    }

    public void setExamples(List<List<String>> examples) {
        this.examples = examples;
    }

    public ScenarioOutline add(List<String> example) {
        examples.add(example);
        return this;
    }
}