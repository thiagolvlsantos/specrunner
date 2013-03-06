package org.specrunner.sql.meta;


public class ConverterDefault implements IConverter {

    public boolean accept(String str) {
        return true;
    }

    public Object convert(String str) {
        return str;
    }
}