/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Auxiliar map of units to methods in Jodatime.
 * 
 * @author Thiago Santos
 *
 */
public class UtilJodatime {

    /**
     * Bind Jodatime method names.
     * 
     * @param map
     */
    public static void bindDatePatterns(Map<String, String> map) {
        map.put("days", "plusDays");
        map.put("weeks", "plusWeeks");
        map.put("months", "plusMonths");
        map.put("years", "plusYears");
    }

    /**
     * Bind time map.
     * 
     * @param map
     *            Map os alias.
     */
    public static void bindTimePatterns(Map<String, String> map) {
        map.put("milliseconds", "plusMillis");
        map.put("seconds", "plusSeconds");
        map.put("minutes", "plusMinutes");
        map.put("hours", "plusHours");
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
    public static <T> T postProcess(Object value, Object[] args, T result, Pattern pattern, Map<String, String> aliasToField, Map<String, String> fieldToMethod) {
        T tmp = result;
        Class<? extends Object> type = result.getClass();
        Matcher m = pattern.matcher(String.valueOf(value).toLowerCase());
        while (m.find()) {
            String all = m.group(0);
            String signal = m.group(2);
            String number = m.group(3);
            all = all.replace(signal, "").replace(number, "").trim();
            String alias = aliasToField.get(all);
            if (alias == null) {
                throw new IllegalArgumentException("Alias '" + alias + "' not found in sr_converters_date_(date|time).properties.");
            }
            String field = fieldToMethod.get(alias);
            if (field == null) {
                throw new IllegalArgumentException("Field '" + field + "' not found in " + fieldToMethod.keySet() + ".");
            }
            try {
                Method met = type.getMethod(field, int.class);
                tmp = (T) met.invoke(tmp, "+".equals(signal) ? Integer.valueOf(number) : -Integer.valueOf(number));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return tmp;
    }

}
