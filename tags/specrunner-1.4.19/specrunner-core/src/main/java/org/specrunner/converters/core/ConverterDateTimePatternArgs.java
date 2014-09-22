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
package org.specrunner.converters.core;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.specrunner.SRServices;
import org.specrunner.converters.ConverterException;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Convert any date (DateTime form Jodatime), given a provided pattern in
 * arg[0].
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateTimePatternArgs extends ConverterNotNullNotEmpty {
    /**
     * Cache of formatters.
     */
    protected static ICache<String, DateTimeFormatter> cache = SRServices.get(ICacheFactory.class).newCache(ConverterDateTimePatternArgs.class.getName());

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        if (value instanceof DateTime) {
            return value;
        }
        try {
            String pattern = String.valueOf(args[0]);
            synchronized (cache) {
                DateTimeFormatter formatter = cache.get(pattern);
                if (formatter == null) {
                    formatter = DateTimeFormat.forPattern(pattern);
                    cache.put(pattern, formatter);
                }
                return formatter.parseDateTime(String.valueOf(value));
            }
        } catch (IllegalArgumentException e) {
            throw new ConverterException(e);
        }
    }
}