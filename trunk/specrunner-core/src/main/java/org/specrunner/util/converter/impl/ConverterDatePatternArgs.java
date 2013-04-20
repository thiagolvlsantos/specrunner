package org.specrunner.util.converter.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.specrunner.util.converter.ConverterException;

/**
 * Convert any date (Date from Java), given a provided pattern in arg[0].
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDatePatternArgs extends ConverterDefault {

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(String.valueOf(args[0])).parse(String.valueOf(value));
        } catch (ParseException e) {
            throw new ConverterException(e);
        }
    }
}