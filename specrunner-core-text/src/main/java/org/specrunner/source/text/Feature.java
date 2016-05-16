/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.source.text;

import java.util.LinkedList;
import java.util.List;

/**
 * Stand for a feature object.
 * 
 * @author Thiago Santos
 * 
 */
public class Feature extends NamedSentences {

    /**
     * Keyword set.
     */
    protected Keywords keywords;
    /**
     * List of background sentences.
     */
    protected List<Sentence> background = new LinkedList<Sentence>();
    /**
     * List of ending sentences.
     */
    protected List<Sentence> finallys = new LinkedList<Sentence>();
    /**
     * List of scenarios.
     */
    protected List<Scenario> scenarios = new LinkedList<Scenario>();

    /**
     * Default constructor.
     * 
     * @param name
     *            The name.
     */
    public Feature(String name) {
        super(name);
    }

    /**
     * Get the keywords used.
     * 
     * @return The keywords.
     */
    public Keywords getKeywords() {
        return keywords;
    }

    /**
     * Set the keywords used.
     * 
     * @param keywords
     *            The keywords.
     */
    public void setKeywords(Keywords keywords) {
        this.keywords = keywords;
    }

    /**
     * The background list.
     * 
     * @return The list.
     */
    public List<Sentence> getBackground() {
        return background;
    }

    /**
     * Set the background list.
     * 
     * @param background
     *            The list.
     */
    public void setBackground(List<Sentence> background) {
        this.background = background;
    }

    /**
     * Add a background sentence.
     * 
     * @param background
     *            A sentence.
     * @return The feature itself.
     */
    public Feature addBackground(Sentence background) {
        this.background.add(background);
        return this;
    }

    /**
     * The ending list.
     * 
     * @return The list.
     */
    public List<Sentence> getFinallys() {
        return finallys;
    }

    /**
     * Set the ending list.
     * 
     * @param finallys
     *            The list.
     */
    public void setFinallys(List<Sentence> finallys) {
        this.finallys = finallys;
    }

    /**
     * Add an ending sentence.
     * 
     * @param finallys
     *            A sentence.
     * @return The feature itself.
     */
    public Feature addFinallys(Sentence finallys) {
        this.finallys.add(finallys);
        return this;
    }

    /**
     * Get the scenario list.
     * 
     * @return The list.
     */
    public List<Scenario> getScenarios() {
        return scenarios;
    }

    /**
     * The scenario list.
     * 
     * @param scenarios
     *            The list.
     */
    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    /**
     * Add a scenario to the feature.
     * 
     * @param scenario
     *            The scenario.
     * @return The feature itself.
     */
    public Feature add(Scenario scenario) {
        scenarios.add(scenario);
        scenario.setParent(this);
        return this;
    }

    /**
     * Validate a feature set.
     * 
     * @return The error message, or null, if no error is found.
     */
    @Override
    public String validate() {
        StringBuilder sb = new StringBuilder();
        if (name == null || name.isEmpty()) {
            sb.append("\nFeature name not found.");
        }
        for (Scenario s : scenarios) {
            sb.append(s.validate());
        }
        return sb.toString();
    }
}
