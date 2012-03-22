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
package org.specrunner.features;

import java.util.Map;

import org.specrunner.configuration.IConfiguration;

/**
 * This is the standard way of parameterizing the framework objects. This is the
 * way of parameterizing all tests in the same execution, to set specific
 * features for each test use IConfiguration infra-structure.
 * 
 * <p>
 * Every class can ready a feature by
 * <code>SpecRunnerService.get(IFeatureManager.class).getFeature("name")</code>.
 * See all <code>IPlugin</code> implementations to check all available features
 * and how to use them.
 * 
 * @author Thiago Santos
 * 
 */
public interface IFeatureManager extends Map<String, Object> {

    /**
     * Set a feature to a value.
     * 
     * @param feature
     *            The feature name.
     * @param value
     *            The value.
     * @return The manager itself.
     */
    IFeatureManager add(String feature, Object value);

    /**
     * Set a feature to a value.
     * 
     * @param feature
     *            The feature name.
     * @param value
     *            The value.
     * @param override
     *            true, if the value should overwrite previous values, false,
     *            otherwise.
     * @return The manager itself.
     */
    IFeatureManager add(String feature, Object value, boolean override);

    /**
     * Sets a feature to an object if the feature exists and the type is
     * compatible.
     * 
     * @param feature
     *            The feature name.
     * @param field
     *            The field name.
     * @param expectedType
     *            The expected type of the feature.
     * @param target
     *            The object where the feature must be set.
     * @throws FeatureManagerException
     *             On object setup.
     */
    void set(String feature, String field, Class<?> expectedType, Object target) throws FeatureManagerException;

    /**
     * Set a set of local configuration as complementary information to feature
     * settings. When a instance of a <code>ISpecRunner</code> received a
     * <code>IConfiguration</code>, this is set here as local and at the
     * beginning of a new test execution it can be just replaced.
     * 
     * @param cfg
     *            Set configuration features.
     */
    void setCfgFeatures(IConfiguration cfg);
}