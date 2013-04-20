package org.specrunner.util.converter.impl;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateTimeCurrent extends ConverterDateTimeCurrentTemplate {

    /**
     * Basic data converter.
     */
    public ConverterDateTimeCurrent() {
        super(new String[] { "atual", "data atual", "data hora atual", "current", "current date", "current timestamp" });
    }
}