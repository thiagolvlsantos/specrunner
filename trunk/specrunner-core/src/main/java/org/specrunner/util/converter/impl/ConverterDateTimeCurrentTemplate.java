package org.specrunner.util.converter.impl;

import org.joda.time.DateTime;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateTimeCurrentTemplate extends AbstractConverterTimeTemplate<DateTime> {

    /**
     * See superclass.
     * 
     * @param regexp
     *            Regexp.
     */
    public ConverterDateTimeCurrentTemplate(String regexp) {
        super(regexp);
    }

    /**
     * See superclass.
     * 
     * @param values
     *            Value.
     */
    public ConverterDateTimeCurrentTemplate(String[] values) {
        super(values);
    }

    @Override
    protected DateTime instance() {
        return new DateTime();
    }
}
