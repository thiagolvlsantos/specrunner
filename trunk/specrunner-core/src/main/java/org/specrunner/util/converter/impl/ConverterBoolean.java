package org.specrunner.util.converter.impl;

/**
 * A tipical boolean converter.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterBoolean extends ConverterBooleanTemplate {

    /**
     * Constructor using 'true' and 'false'.
     */
    public ConverterBoolean() {
        super(Boolean.TRUE.toString(), Boolean.FALSE.toString());
    }
}
