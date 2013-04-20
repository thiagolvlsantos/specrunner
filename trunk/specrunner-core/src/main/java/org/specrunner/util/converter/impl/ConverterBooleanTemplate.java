package org.specrunner.util.converter.impl;

import org.specrunner.util.converter.ConverterException;

/**
 * The boolean template converter.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class ConverterBooleanTemplate extends ConverterDefault {

    /**
     * String for '<code>TRUE</code>'.
     */
    private String yes;
    /**
     * String for '<code>FALSE</code>'.
     */
    private String no;

    /**
     * Creates boolean converter using some strings for 'true'and 'false'.
     * 
     * @param yes
     *            The TRUE string.
     * @param no
     *            The FALSE string.
     */
    public ConverterBooleanTemplate(String yes, String no) {
        this.yes = yes.toLowerCase().trim();
        this.no = no.toLowerCase().trim();
    }

    @Override
    public Object convert(Object value, Object[] args) throws ConverterException {
        if (value == null) {
            return null;
        }
        String tmp = String.valueOf(value).toLowerCase().trim();
        if (yes.equals(tmp)) {
            return Boolean.TRUE;
        } else if (no.equals(tmp)) {
            return Boolean.FALSE;
        } else {
            throw new ConverterException("Value '" + tmp + "' not valid, use \n'" + yes + "' or '" + no + "'");
        }
    }
}
