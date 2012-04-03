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
package org.specrunner.util.comparer.impl;

import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.util.UtilLog;
import org.specrunner.util.comparer.IComparator;

/**
 * Abstract comparator of time objects.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractComparatorTime implements IComparator {

    /**
     * Feature to set time tolerance.
     */
    public static final String FEATURE_TOLERANCE = AbstractComparatorTime.class.getName() + ".tolerance";
    /**
     * Tolerance for comparations.
     */
    protected Long tolerance = 0L;

    @Override
    public void initialize() {
        try {
            IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
            fh.set(FEATURE_TOLERANCE, "tolerance", Long.class, this);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
    }

    /**
     * Get the time tolerance.
     * 
     * @return The time.
     */
    public Long getTolerance() {
        return tolerance;
    }

    /**
     * Set time tolerance.
     * 
     * @param tolerance
     *            The tolerance.
     */
    public void setTolerance(Long tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Compare the expected and received.
     * 
     * @param left
     *            The expected time.
     * @param right
     *            The received time.
     * @return true, if acceptable, false, otherwise.
     */
    protected boolean compare(long left, long right) {
        long gap = Math.max(left, right) - Math.min(left, right);
        return gap <= tolerance;
    }
}
