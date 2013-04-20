package org.specrunner.util.converter.impl;

import org.specrunner.util.converter.ConverterException;

/**
 * Create time information.
 * 
 * @author Thiago Santos
 * 
 * @param <T>
 *            Date object.
 */
@SuppressWarnings("serial")
public abstract class AbstractConverterTimeTemplate<T> extends ConverterDefault {

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
    public AbstractConverterTimeTemplate(String[] values) {
        this.values = values;
    }

    /**
     * Constructor using a regular expression.
     * 
     * @param regexp
     *            The regular expression to match date.
     */
    public AbstractConverterTimeTemplate(String regexp) {
        this.regexp = regexp;
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        String str = String.valueOf(value);
        if (regexp != null && str.matches(regexp)) {
            return instance();
        }
        if (values != null) {
            for (String v : values) {
                if (str.contains(v)) {
                    return instance();
                }
            }
        }
        throw new ConverterException("Invalid value '" + value + "'.");
    }

    /**
     * Creates an instance of date.
     * 
     * @return Something aka date.
     */
    protected abstract T instance();
}
