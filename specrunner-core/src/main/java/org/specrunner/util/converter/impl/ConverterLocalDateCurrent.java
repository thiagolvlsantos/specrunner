package org.specrunner.util.converter.impl;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterLocalDateCurrent extends ConverterLocalDateCurrentTemplate {

    /**
     * Basic data converter.
     */
    public ConverterLocalDateCurrent() {
        super(new String[] { "atual", "data atual", "current", "current date" });
    }
}