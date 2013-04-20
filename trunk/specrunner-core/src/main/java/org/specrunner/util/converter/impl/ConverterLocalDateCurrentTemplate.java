package org.specrunner.util.converter.impl;

import org.joda.time.LocalDate;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterLocalDateCurrentTemplate extends AbstractConverterTimeTemplate<LocalDate> {

    /**
     * See superclass.
     * 
     * @param regexp
     *            Regexp.
     */
    public ConverterLocalDateCurrentTemplate(String regexp) {
        super(regexp);
    }

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterLocalDateCurrentTemplate(String[] values) {
        super(values);
    }

    @Override
    protected LocalDate instance() {
        return new LocalDate();
    }
}
