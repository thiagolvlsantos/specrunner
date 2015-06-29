/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

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
package org.specrunner.formatters.core;

import org.specrunner.SRServices;
import org.specrunner.formatters.FormatterException;
import org.specrunner.formatters.IFormatter;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Create time pattern formatter.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            Any object.
 */
@SuppressWarnings("serial")
public abstract class AbstractFormatterTime<T> implements IFormatter {

    /**
     * Cache of formatters.
     */
    protected ICache<String, T> cache = SRServices.get(ICacheFactory.class).newCache(AbstractFormatterTime.class.getName());

    @Override
    public void initialize() {
        // nop.
    }

    @Override
    public String format(Object value, Object[] args) throws FormatterException {
        if (testType(value)) {
            String str = String.valueOf(args[0]);
            T pattern = cache.get(str);
            if (pattern == null) {
                pattern = newInstance(str);
                cache.put(str, pattern);
            }
            return format(pattern, value, args);
        }
        return String.valueOf(value);
    }

    protected abstract boolean testType(Object value);

    protected abstract T newInstance(String str);

    protected abstract String format(T pattern, Object value, Object[] args);
}