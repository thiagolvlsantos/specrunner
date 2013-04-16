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
package org.specrunner.sql;

import org.specrunner.SpecRunnerServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPluginValue;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Failure;
import org.specrunner.result.status.Success;
import org.specrunner.reuse.IReuseManager;
import org.specrunner.sql.util.StringUtil;
import org.specrunner.util.UtilLog;

/**
 * Perform release of an Database with a given name.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginRelease extends AbstractPluginValue {

    /**
     * Database name feature.
     */
    public static final String FEATURE_NAME = PluginRelease.class.getName() + ".name";

    /**
     * Feature for names separators.
     */
    public static final String FEATURE_SEPARATOR = PluginRelease.class.getName() + ".separator";
    /**
     * Default separator.
     */
    public static final String DEFAULT_SEPARATOR = ";";
    /**
     * The separator, default is ";".
     */
    private String separator = DEFAULT_SEPARATOR;

    /**
     * Get the name separator.
     * 
     * @return The separator.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Set the name separator.
     * 
     * @param separator
     *            The separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        if (getName() == null) {
            fm.set(FEATURE_NAME, this);
        }
        fm.set(FEATURE_SEPARATOR, this);
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        String[] bases = StringUtil.parts(getName() != null ? getName() : PluginDatabase.DEFAULT_DATABASE_NAME, separator);
        int failure = 0;
        for (String base : bases) {
            IDatabase database = PluginDatabase.getDatabase(context, base);
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info("PluginRelease database:" + database);
            }
            try {
                // only not reusable instances can be released this way,
                // otherwise reuse manager will release in the right time.
                if (SpecRunnerServices.get(IReuseManager.class).get(base) == null) {
                    database.release();
                } else {
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("PluginRelease reusable database:" + base + " not release.");
                    }
                }
            } catch (Exception e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                failure++;
                result.addResult(Failure.INSTANCE, context.peek(), new PluginException("Error in database:" + base + ". Error:" + e.getMessage(), e));
            }
        }
        if (failure == 0) {
            result.addResult(Success.INSTANCE, context.peek());
        }
        return ENext.DEEP;
    }
}