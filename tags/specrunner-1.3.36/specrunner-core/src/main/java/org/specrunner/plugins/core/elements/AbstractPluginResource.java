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
package org.specrunner.plugins.core.elements;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;

/**
 * Helper class for resources plugins.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractPluginResource extends AbstractPlugin {

    /**
     * Default behavior is save resources. To speed up test execution can be
     * disabled.
     */
    public static final String FEATURE_SAVE = AbstractPluginResource.class.getName() + ".save";
    /**
     * Set the save status.
     */
    private Boolean save = null;

    /**
     * Constructor.
     */
    protected AbstractPluginResource() {
    }

    /**
     * Return true, if resources like CSS/JS should saved on error report.
     * 
     * @return true, if save is enabled, false, otherwise.
     */
    public Boolean getSave() {
        return save;
    }

    /**
     * Set save state.
     * 
     * @param save
     *            The new state.
     */
    public void setSave(Boolean save) {
        this.save = save;
    }

    /**
     * Check if save enabled or not.
     * 
     * @return true, is save is enabled, false, otherwise.
     */
    protected boolean isSave() {
        return save == null || save;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        if (save == null) {
            IFeatureManager fm = SRServices.getFeatureManager();
            fm.set(FEATURE_SAVE, this);
        }
    }
}