package org.specrunner.sql.meta.converter;

import org.specrunner.sql.meta.IConverter;

public class ConverterLong implements IConverter {

    public boolean accept(String str) {
        return str.matches("[0-9]");
    }

    public Object convert(String str) {
        return Long.valueOf(str);
    }
}