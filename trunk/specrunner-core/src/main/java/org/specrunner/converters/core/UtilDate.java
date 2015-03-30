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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.specrunner.SRServices;
import org.specrunner.properties.IPropertyLoader;
import org.specrunner.properties.PropertyLoaderException;

/**
 * Auxiliar map of units to methods in java.util.Date/Calendar.
 * 
 * @author Thiago Santos
 *
 */
public class UtilDate {

    public static void bindAliases(Map<String, String> map) {
        bindDateAliases(map);
        bindTimeAliases(map);
    }

    public static void bindDateAliases(Map<String, String> map) {
        try {
            add(map, SRServices.get(IPropertyLoader.class).load("sr_converters_date_date.properties"));
        } catch (PropertyLoaderException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void bindTimeAliases(Map<String, String> map) {
        try {
            add(map, SRServices.get(IPropertyLoader.class).load("sr_converters_date_time.properties"));
        } catch (PropertyLoaderException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void add(Map<String, String> map, List<Properties> load) {
        for (Properties p : load) {
            for (Entry<Object, Object> e : p.entrySet()) {
                map.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }
        }
    }

    /**
     * Bind Jodatime method names.
     * 
     * @param map
     */
    public static void bindPatterns(Map<String, Integer> map) {
        map.put("days", Calendar.DAY_OF_MONTH);
        map.put("weeks", Calendar.WEEK_OF_MONTH);
        map.put("months", Calendar.MONTH);
        map.put("years", Calendar.YEAR);
        map.put("milliseconds", Calendar.MILLISECOND);
        map.put("seconds", Calendar.SECOND);
        map.put("minutes", Calendar.MINUTE);
        map.put("hours", Calendar.HOUR_OF_DAY);
    }

    /**
     * Get pattern for a map.
     * 
     * @param set
     *            A set.
     * @return The pattern for a given map.
     */
    public static Pattern extractPattern(Set<String> set) {
        String keys = set.toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "|");
        String str = "(([\\+|\\-])[\\s\\n]*(\\d+)[\\s\\n]*(" + keys + "))+";
        return Pattern.compile(str);
    }

    /**
     * Post process an object.
     * 
     * @param value
     *            The value.
     * @param args
     *            The arguments.
     * @param result
     *            The result.
     * @param pattern
     *            The pattern.
     * @param map
     *            A map.
     * @return A changed instance of 'result'.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Date> T postProcess(Object value, Object[] args, Calendar calendar, T result, Pattern pattern, Map<String, String> aliasToField, Map<String, Integer> fieldToMethod) {
        calendar.setTime(result);
        Class<? extends Date> type = result.getClass();
        Matcher m = pattern.matcher(String.valueOf(value).toLowerCase());
        boolean found = false;
        while (m.find()) {
            found = true;
            String all = m.group(0);
            String signal = m.group(2);
            String number = m.group(3);
            all = all.replace(signal, "").replace(number, "").trim();
            String alias = aliasToField.get(all);
            if (alias == null) {
                throw new IllegalArgumentException("Alias '" + alias + "' not found in sr_converters_date_(date|time).properties.");
            }
            Integer field = fieldToMethod.get(alias);
            if (field == null) {
                throw new IllegalArgumentException("Field '" + field + "' not found in " + fieldToMethod.keySet() + ".");
            }
            calendar.add(field, Integer.valueOf(number));
        }
        if (found) {
            try {
                return (T) type.getConstructor(long.class).newInstance(calendar.getTimeInMillis());
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return result;
    }
}