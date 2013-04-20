package org.specrunner.util.converter.impl;

import java.util.Date;

import org.specrunner.util.converter.ConverterException;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateCurrentTemplate extends ConverterDefault {

    /**
     * Strings that stand for 'current date'.
     */
    private String[] values;
    /**
     * Pattern for 'current date'.
     */
    private String regexp;

    /**
     * Constructor using strings.
     * 
     * @param values
     *            The values to be converted to date.
     */
    public ConverterDateCurrentTemplate(String[] values) {
        this.values = values;
    }

    /**
     * Constructor using a regular expression.
     * 
     * @param regexp
     *            The regular expression to match date.
     */
    public ConverterDateCurrentTemplate(String regexp) {
        this.regexp = regexp;
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        String str = String.valueOf(value);
        if (regexp != null && str.matches(regexp)) {
            return new Date();
        }
        if (values != null) {
            for (String v : values) {
                if (str.contains(v)) {
                    return new Date();
                }
            }
        }
        throw new ConverterException("Invalid value '" + value + "'.");
    }
}
