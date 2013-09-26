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

public class Feature extends Description {

    private List<String> background = new LinkedList<String>();
    private List<String> finallys = new LinkedList<String>();
    private List<Scenario> scenarios = new LinkedList<Scenario>();

    public Feature(String name) {
        super(name);
    }

    public List<String> getBackground() {
        return background;
    }

    public void setBackground(List<String> background) {
        this.background = background;
    }

    public Feature addBackground(String background) {
        this.background.add(background);
        return this;
    }

    public List<String> getFinallys() {
        return finallys;
    }

    public void setFinallys(List<String> finallys) {
        this.finallys = finallys;
    }

    public Feature addFinallys(String finallys) {
        this.finallys.add(finallys);
        return this;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    public Feature add(Scenario scenario) {
        scenarios.add(scenario);
        scenario.setParent(this);
        return this;
    }
}