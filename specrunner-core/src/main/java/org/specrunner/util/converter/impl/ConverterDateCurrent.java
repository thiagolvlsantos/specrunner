package org.specrunner.util.converter.impl;

/**
 * Create current date.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterDateCurrent extends ConverterDateCurrentTemplate {

    /**
     * Basic data converter.
     */
    public ConverterDateCurrent() {
        super(new String[] { "atual", "data atual", "data hora atual", "current", "current date", "current timestamp" });
    }
}