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

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Auxiliar map of units to methods in java.util.Date/Calendar.
 * 
 * @author Thiago Santos
 *
 */
public class UtilDate {

    /**
     * Bind Jodatime method names.
     * 
     * @param map
     */
    public static void bindPatterns(Map<String, Integer> map) {
        bindEnglish(map);
        bindPortuguese(map);
    }

    /**
     * Bind English words.
     * 
     * @param map
     *            A map.
     */
    public static void bindEnglish(Map<String, Integer> map) {
        bindEnglishDate(map);
        bindEnglishTime(map);
    }

    /**
     * Bind English date vocabulary.
     * 
     * @param map
     *            A map.
     */
    public static void bindEnglishDate(Map<String, Integer> map) {
        map.put("days", Calendar.DAY_OF_MONTH);
        map.put("day", Calendar.DAY_OF_MONTH);
        map.put("weeks", Calendar.WEEK_OF_MONTH);
        map.put("week", Calendar.WEEK_OF_MONTH);
        map.put("months", Calendar.MONTH);
        map.put("month", Calendar.MONTH);
        map.put("years", Calendar.YEAR);
        map.put("year", Calendar.YEAR);
    }

    /**
     * Bind English time vocabulary.
     * 
     * @param map
     *            A map.
     */
    public static void bindEnglishTime(Map<String, Integer> map) {
        map.put("miliseconds", Calendar.MILLISECOND);
        map.put("milisecond", Calendar.MILLISECOND);
        map.put("seconds", Calendar.SECOND);
        map.put("second", Calendar.SECOND);
        map.put("minutes", Calendar.MINUTE);
        map.put("minute", Calendar.MINUTE);
        map.put("hours", Calendar.HOUR_OF_DAY);
        map.put("hour", Calendar.HOUR_OF_DAY);
    }

    /**
     * Bind Portuguese words.
     * 
     * @param map
     *            A map.
     */
    public static void bindPortuguese(Map<String, Integer> map) {
        bindPortugueseDate(map);
        bindPortugueseTime(map);
    }

    /**
     * Bind Portuguese date vocabulary.
     * 
     * @param map
     *            A map.
     */
    public static void bindPortugueseDate(Map<String, Integer> map) {
        map.put("dias", Calendar.DAY_OF_MONTH);
        map.put("dia", Calendar.DAY_OF_MONTH);
        map.put("semanas", Calendar.WEEK_OF_MONTH);
        map.put("semana", Calendar.WEEK_OF_MONTH);
        map.put("meses", Calendar.MONTH);
        map.put("mês", Calendar.MONTH);
        map.put("mes", Calendar.MONTH);
        map.put("anos", Calendar.YEAR);
        map.put("ano", Calendar.YEAR);
    }

    /**
     * Bind Portuguese time vocabulary.
     * 
     * @param map
     *            A map.
     */
    public static void bindPortugueseTime(Map<String, Integer> map) {
        map.put("milisegundos", Calendar.MILLISECOND);
        map.put("milisegundo", Calendar.MILLISECOND);
        map.put("segundos", Calendar.SECOND);
        map.put("segundo", Calendar.SECOND);
        map.put("minutos", Calendar.MINUTE);
        map.put("minuto", Calendar.MINUTE);
        map.put("horas", Calendar.HOUR_OF_DAY);
        map.put("hora", Calendar.HOUR_OF_DAY);
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
    public static <T extends Date> T postProcess(Object value, Object[] args, Calendar calendar, T result, Pattern pattern, Map<String, Integer> map) {
        calendar.setTime(result);
        Class<? extends Date> type = result.getClass();
        Method met;
        try {
            met = Calendar.class.getMethod("add", int.class, int.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        Matcher m = pattern.matcher(String.valueOf(value).toLowerCase());
        while (m.find()) {
            String all = m.group(0);
            String signal = m.group(2);
            String number = m.group(3);
            all = all.replace(signal, "").replace(number, "").trim();
            try {
                met.invoke(calendar, map.get(all), Integer.valueOf(number));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        try {
            return (T) type.getConstructor(long.class).newInstance(calendar.getTimeInMillis());
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
