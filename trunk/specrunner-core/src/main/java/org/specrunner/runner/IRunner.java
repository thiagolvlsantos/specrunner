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
package org.specrunner.runner;

import java.util.List;

import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPlugin;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;

/**
 * Perform a specification.
 * 
 * @author Thiago Santos
 * 
 */
public interface IRunner {

    /**
     * Feature to set the disabled aliases using IFeatureManager or
     * IConfiguration.
     */
    String FEATURE_DISABLED_ALIASES = IRunner.class.getName() + ".disabledAliases";

    /**
     * Feature to set the enabled aliases using IFeatureManager or
     * IConfiguration.
     */
    String FEATURE_ENABLED_ALIASES = IRunner.class.getName() + ".enabledAliases";

    /**
     * Feature to set the disabled type using IFeatureManager or IConfiguration.
     */
    String FEATURE_DISABLED_TYPES = IRunner.class.getName() + ".disabledTypes";

    /**
     * Feature to set the enabled types using IFeatureManager or IConfiguration.
     */
    String FEATURE_ENABLED_TYPES = IRunner.class.getName() + ".enabledTypes";

    /**
     * List of alias that should be ignored by runner.
     * 
     * @param disabledAliases
     *            The alias to be ignored by runner.
     */
    void setDisabledAliases(List<String> disabledAliases);

    /**
     * Get the list of disabled aliases.
     * 
     * @return Disabled aliases list.
     */
    List<String> getDisabledAliases();

    /**
     * List of alias that should be enabled by runner.
     * 
     * @param enabledAliases
     *            The alias to be enabled by runner.
     */
    void setEnabledAliases(List<String> enabledAliases);

    /**
     * Get the list of enabled aliases.
     * 
     * @return Enabled aliases list.
     */
    List<String> getEnabledAliases();

    /**
     * List of action types that should be ignored by runner.
     * 
     * @param disabledTypes
     *            The types to be ignored by runner.
     */
    void setDisabledTypes(List<? extends ActionType> disabledTypes);

    /**
     * Get the list of disabled types.
     * 
     * @return Disabled types list.
     */
    List<? extends ActionType> getDisabledTypes();

    /**
     * List of action types that should be enabled by runner.
     * 
     * @param enabledTypes
     *            The types to be enabled by runner.
     */
    void setEnabledTypes(List<? extends ActionType> enabledTypes);

    /**
     * Get the list of enabled types.
     * 
     * @return Enabled types list.
     */
    List<? extends ActionType> getEnabledTypes();

    /**
     * Performs the specification in source.
     * 
     * @param source
     *            The specification.
     * @param context
     *            A context.
     * @param result
     *            A result set.
     * @throws RunnerException
     *             On execution fail.
     */
    void run(ISource source, IContext context, IResultSet result) throws RunnerException;

    /**
     * Performs the specification node.
     * 
     * @param node
     *            A specification node.
     * @param context
     *            A context.
     * @param result
     *            A result set.
     * @throws RunnerException
     *             On execution fail.
     */
    void run(Node node, IContext context, IResultSet result) throws RunnerException;

    /**
     * Performs a plugin without a node specification.
     * 
     * @param plugin
     *            A plugin.
     * @param context
     *            A context.
     * @param result
     *            A result set.
     * @throws RunnerException
     *             On execution fail.
     */
    void run(IPlugin plugin, IContext context, IResultSet result) throws RunnerException;
}