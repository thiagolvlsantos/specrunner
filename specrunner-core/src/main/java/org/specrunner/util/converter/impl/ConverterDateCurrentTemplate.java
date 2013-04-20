package org.specrunner.util.converter.impl;

import java.util.Date;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateCurrentTemplate extends AbstractConverterTimeTemplate<Date> {

    /**
     * See superclass.
     * 
     * @param regexp
     *            Regexp.
     */
    public ConverterDateCurrentTemplate(String regexp) {
        super(regexp);
    }

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterDateCurrentTemplate(String[] values) {
        super(values);
    }

    @Override
    protected Date instance() {
        return new Date();
    }
}
