package org.specrunner.util.converter.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.specrunner.util.converter.ConverterException;

/**
 * General date converter.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDatePatternTemplate extends ConverterDefault {

    /**
     * Parser instance.
     */
    private SimpleDateFormat pattern;

    /**
     * Create a SimpleDateFormat using the given pattern.
     * 
     * @param pattern
     *            Pattern.
     */
    public ConverterDatePatternTemplate(String pattern) {
        this.pattern = new SimpleDateFormat(pattern);
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        try {
            return pattern.parse(String.valueOf(value));
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
}