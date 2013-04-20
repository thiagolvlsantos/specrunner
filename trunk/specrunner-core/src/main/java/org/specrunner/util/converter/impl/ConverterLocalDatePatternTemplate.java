package org.specrunner.util.converter.impl;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.specrunner.util.converter.ConverterException;

/**
 * General date converter.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterLocalDatePatternTemplate extends ConverterDefault {

    /**
     * Parser instance.
     */
    private DateTimeFormatter pattern;

    /**
     * Create a SimpleDateFormat using the given pattern.
     * 
     * @param pattern
     *            Pattern.
     */
    public ConverterLocalDatePatternTemplate(String pattern) {
        this.pattern = DateTimeFormat.forPattern(pattern);
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        try {
            return pattern.parseDateTime(String.valueOf(value)).toLocalDate();
        } catch (IllegalArgumentException e) {
            throw new ConverterException(e);
        }
    }
}