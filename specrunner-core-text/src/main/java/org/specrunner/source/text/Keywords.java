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
package org.specrunner.source.text;

import java.util.ResourceBundle;

/**
 * Map of special keywords.
 * 
 * @author Thiago Santos
 * 
 */
public class Keywords {

    /**
     * Feature keyword.
     */
    protected static final String FEATURE = "feature";
    /**
     * Background keyword.
     */
    protected static final String BACKGROUND = "background";
    /**
     * Finally keyword.
     */
    protected static final String FINALLY = "finally";
    /**
     * Scenario keyword.
     */
    protected static final String SCENARIO = "scenario";
    /**
     * Outline keyword.
     */
    protected static final String SCENARIO_OUTLINE = "outline";
    /**
     * Examples keyword.
     */
    protected static final String EXAMPLES = "examples";
    /**
     * Bundle.
     */
    protected ResourceBundle bundle;

    /**
     * Create a keyword set with a bundle.
     * 
     * @param bundle
     *            The bundle.
     */
    public Keywords(ResourceBundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("Missing bundle information.");
        }
        this.bundle = bundle;
    }

    /**
     * Get the i18n feature keyword.
     * 
     * @return The feature keyword.
     */
    public String getFeature() {
        return bundle.getString(FEATURE);
    }

    /**
     * Get the i18n background keyword.
     * 
     * @return The background keyword.
     */
    public String getBackground() {
        return bundle.getString(BACKGROUND);
    }

    /**
     * Get the i18n finally keyword.
     * 
     * @return The finally keyword.
     */
    public String getFinally() {
        return bundle.getString(FINALLY);
    }

    /**
     * Get the i18n scenario keyword.
     * 
     * @return The scenario keyword.
     */
    public String getScenario() {
        return bundle.getString(SCENARIO);
    }

    /**
     * Get the i18n scenario outline keyword.
     * 
     * @return The scenario outline keyword.
     */
    public String getScenarioOutline() {
        return bundle.getString(SCENARIO_OUTLINE);
    }

    /**
     * Get the i18n examples keyword.
     * 
     * @return The examples keyword.
     */
    public String getExamples() {
        return bundle.getString(EXAMPLES);
    }
}
