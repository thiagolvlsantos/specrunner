package org.specrunner.util.converter.impl;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.specrunner.util.converter.ConverterException;

/**
 * Convert any date (DateTime form Jodatime), given a provided pattern in
 * arg[0].
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterLocalDatePatternArgs extends ConverterDefault {

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(String.valueOf(args[0]));
            return formatter.parseDateTime(String.valueOf(value)).toLocalDate();
        } catch (IllegalArgumentException e) {
            throw new ConverterException(e);
        }
    }
}