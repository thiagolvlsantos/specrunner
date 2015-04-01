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
    public static void bindPatterns(Map<String, String> map) {
        bindEnglish(map);
        bindPortuguese(map);
    }

    /**
     * Bind English words.
     * 
     * @param map
     *            A map.
     */
    public static void bindEnglish(Map<String, String> map) {
        bindEnglishDate(map);
        bindEnglishTime(map);
    }

    /**
     * Bind English date vocabulary.
     * 
     * @param map
     *            A map.
     */
    public static void bindEnglishDate(Map<String, String> map) {
        map.put("days", "plusDays");
        map.put("day", "plusDays");
        map.put("weeks", "plusWeeks");
        map.put("week", "plusWeeks");
        map.put("months", "plusMonths");
        map.put("month", "plusMonths");
        map.put("years", "plusYears");
        map.put("year", "plusYears");
    }

    /**
     * Bind English time vocabulary.
     * 
     * @param map
     *            A map.
     */
    public static void bindEnglishTime(Map<String, String> map) {
        map.put("miliseconds", "plusMillis");
        map.put("milisecond", "plusMillis");
        map.put("seconds", "plusSeconds");
        map.put("second", "plusSeconds");
        map.put("minutes", "plusMinutes");
        map.put("minute", "plusMinutes");
        map.put("hours", "plusHours");
        map.put("hour", "plusHours");
    }

    /**
     * Bind Portuguese words.
     * 
     * @param map
     *            A map.
     */
    public static void bindPortuguese(Map<String, String> map) {
        bindPortugueseDate(map);
        bindPortugueseTime(map);
    }

    /**
     * Bind Portuguese date vocabulary.
     * 
     * @param map
     *            A map.
     */
    public static void bindPortugueseDate(Map<String, String> map) {
        map.put("dias", "plusDays");
        map.put("dia", "plusDays");
        map.put("semanas", "plusWeeks");
        map.put("semana", "plusWeeks");
        map.put("meses", "plusMonths");
        map.put("mês", "plusMonths");
        map.put("anos", "plusYears");
        map.put("ano", "plusYears");
    }

    /**
     * Bind Portuguese time vocabulary.
     * 
     * @param map
     *            A map.
     */
    public static void bindPortugueseTime(Map<String, String> map) {
        map.put("milisegundos", "plusMillis");
        map.put("milisegundo", "plusMillis");
        map.put("segundos", "plusSeconds");
        map.put("segundo", "plusSeconds");
        map.put("minutos", "plusMinutes");
        map.put("minuto", "plusMinutes");
        map.put("horas", "plusHours");
        map.put("hora", "plusHours");
    }

    /**
     * Get pattern for a map.
     * 
     * @param map
     *            A map.
     * @return The pattern for a given map.
     */
    public static Pattern extractPattern(Map<String, String> map) {
        String keys = map.keySet().toString().replace("[", "").replace("]", "").replace(" ", "").replace(",", "|");
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
    public static <T> T postProcess(Object value, Object[] args, T result, Pattern pattern, Map<String, String> map) {
        T tmp = result;
        Class<? extends Object> type = result.getClass();
        Matcher m = pattern.matcher(String.valueOf(value).toLowerCase());
        while (m.find()) {
            String all = m.group(0);
            String signal = m.group(2);
            String number = m.group(3);
            all = all.replace(signal, "").replace(number, "").trim();
            try {
                Method met = type.getMethod(map.get(all), int.class);
                tmp = (T) met.invoke(tmp, Integer.valueOf(number));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return tmp;
    }
}
