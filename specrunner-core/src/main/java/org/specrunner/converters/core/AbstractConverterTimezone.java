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
package org.specrunner.converters.core;

import java.util.TimeZone;

import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Create timezone information.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            Date object.
 */
@SuppressWarnings("serial")
public abstract class AbstractConverterTimezone<T> extends ConverterNotNullNotEmpty {

    /**
     * Cache of timezone.
     */
    protected static ICache<String, TimeZone> cacheTimezone = SRServices.get(ICacheFactory.class).newCache(AbstractConverterTimezone.class.getName());

    /**
     * Feature to set timezone.
     */
    public static final String FEATURE_TIMEZONE = AbstractConverterTimezone.class.getName() + ".timezone";
    /**
     * The date timezone for adjust.
     */
    private String timezone;

    /**
     * Get current timezone set.
     * 
     * @return The current timezone.
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Set current timezone.
     * 
     * @param timezone
     *            The timezone.
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public void initialize() {
        super.initialize();
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.set(FEATURE_TIMEZONE, this);
    }

    /**
     * Get a timezone definition.
     * 
     * @return A timezone, of name is valid, false, otherwise.
     */
    protected TimeZone getTimeZone() {
        String name = getTimezone();
        if (name != null) {
            synchronized (cacheTimezone) {
                TimeZone tz = cacheTimezone.get(name);
                if (tz == null) {
                    tz = TimeZone.getTimeZone(name);
                    if (tz == null) {
                        throw new RuntimeException("Invalid timezone:" + name);
                    }
                    cacheTimezone.put(name, tz);
                }
                return tz;
            }
        }
        return null;
    }
}