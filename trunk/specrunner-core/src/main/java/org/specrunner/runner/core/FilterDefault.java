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
package org.specrunner.runner.core;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.context.IBlock;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.IPlugin;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.PluginNop;
import org.specrunner.runner.IFilter;
import org.specrunner.util.UtilLog;

/**
 * Default filter by alias type, and by action type.
 * 
 * @author Thiago Santos
 * 
 */
public class FilterDefault implements IFilter {

    /**
     * Feature to set the disabled aliases using IFeatureManager or
     * IConfiguration.
     */
    public static final String FEATURE_DISABLED_ALIASES = FilterDefault.class.getName() + ".disabledAliases";

    /**
     * Feature to set the enabled aliases using IFeatureManager or
     * IConfiguration.
     */
    public static final String FEATURE_ENABLED_ALIASES = FilterDefault.class.getName() + ".enabledAliases";

    /**
     * Feature to set the disabled type using IFeatureManager or IConfiguration.
     */
    public static final String FEATURE_DISABLED_TYPES = FilterDefault.class.getName() + ".disabledTypes";

    /**
     * Feature to set the enabled types using IFeatureManager or IConfiguration.
     */
    public static final String FEATURE_ENABLED_TYPES = FilterDefault.class.getName() + ".enabledTypes";

    /**
     * Thread safe filter instance.
     */
    public static final ThreadLocal<FilterDefault> INSTANCE = new ThreadLocal<FilterDefault>() {
        @Override
        protected FilterDefault initialValue() {
            return new FilterDefault();
        }
    };

    /**
     * List of disabled aliases.
     */
    protected List<String> disabledAliases;
    /**
     * List of enabled aliases.
     */
    protected List<String> enabledAliases;

    /**
     * List of disabled types.
     */
    protected List<? extends ActionType> disabledTypes;
    /**
     * List of enabled types.
     */
    protected List<? extends ActionType> enabledTypes;

    /**
     * Show message on error.
     */
    protected boolean showMessage = true;

    /**
     * List of alias that should be ignored by runner.
     * 
     * @param disabledAliases
     *            The alias to be ignored by runner.
     */
    public void setDisabledAliases(List<String> disabledAliases) {
        if (disabledAliases == null) {
            this.disabledAliases = null;
        } else {
            this.disabledAliases = new LinkedList<String>();
            for (String s : disabledAliases) {
                if (s != null) {
                    this.disabledAliases.add(s.toLowerCase());
                }
            }
        }
    }

    /**
     * Get the list of disabled aliases.
     * 
     * @return Disabled aliases list.
     */
    public List<String> getDisabledAliases() {
        return disabledAliases;
    }

    /**
     * List of alias that should be enabled by runner.
     * 
     * @param enabledAliases
     *            The alias to be enabled by runner.
     */
    public void setEnabledAliases(List<String> enabledAliases) {
        if (enabledAliases == null) {
            this.enabledAliases = null;
        } else {
            this.enabledAliases = new LinkedList<String>();
            for (String s : enabledAliases) {
                if (s != null) {
                    this.enabledAliases.add(s.toLowerCase());
                }
            }
        }
    }

    /**
     * Get the list of enabled aliases.
     * 
     * @return Enabled aliases list.
     */
    public List<String> getEnabledAliases() {
        return enabledAliases;
    }

    /**
     * List of action types that should be ignored by runner.
     * 
     * @param disabledTypes
     *            The types to be ignored by runner.
     */
    public void setDisabledTypes(List<? extends ActionType> disabledTypes) {
        if (disabledTypes == null) {
            this.disabledTypes = null;
        } else {
            this.disabledTypes = new LinkedList<ActionType>(disabledTypes);
        }
    }

    /**
     * Get the list of disabled types.
     * 
     * @return Disabled types list.
     */
    public List<? extends ActionType> getDisabledTypes() {
        return disabledTypes;
    }

    /**
     * List of action types that should be enabled by runner.
     * 
     * @param enabledTypes
     *            The types to be enabled by runner.
     */
    public void setEnabledTypes(List<? extends ActionType> enabledTypes) {
        if (enabledTypes == null) {
            this.enabledTypes = null;
        } else {
            this.enabledTypes = new LinkedList<ActionType>(enabledTypes);
        }
    }

    /**
     * Get the list of enabled types.
     * 
     * @return Enabled types list.
     */
    public List<? extends ActionType> getEnabledTypes() {
        return enabledTypes;
    }

    /**
     * Indicate runner to show message on not accepted blocks.
     * 
     * @return <code>true</code>, to show message, <code>false</code>,
     *         otherwise.
     */
    public boolean isShowMessage() {
        return showMessage;
    }

    /**
     * Set the message status.
     * 
     * @param showMessage
     *            The message flag.
     */
    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    @Override
    public void initialize(IContext context) {
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_DISABLED_ALIASES, this);
        fm.set(FEATURE_ENABLED_ALIASES, this);
        fm.set(FEATURE_DISABLED_TYPES, this);
        fm.set(FEATURE_ENABLED_TYPES, this);
    }

    @Override
    public boolean accept(IBlock block) {
        IPluginFactory factory = SRServices.get(IPluginFactory.class);
        IPlugin plugin = block.getPlugin();
        if (plugin != PluginNop.emptyPlugin()) {
            try {
                // ignore by alias
                String alias = factory.getAlias(plugin.getClass());
                boolean hasDisabled = disabledAliases != null && disabledAliases.contains(alias);
                boolean hasEnabled = enabledAliases != null && !enabledAliases.contains(alias);
                if (alias != null && hasDisabled || hasEnabled) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Plugin '" + alias + "' ignored.");
                    }
                    return false;
                }
                // ignore by type
                ActionType type = plugin.getActionType();
                boolean actionDisabled = disabledTypes != null && disabledTypes.contains(type);
                boolean actionEnabled = enabledTypes != null && !enabledTypes.contains(type);
                if (type != null && actionDisabled || actionEnabled) {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("Plugin '" + type.asString() + "' ignored.");
                    }
                    return false;
                }
            } catch (PluginException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug("Plugin '" + plugin.getClass() + "' has no alias.");
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public boolean showMessage(IBlock block) {
        return showMessage;
    }
}
