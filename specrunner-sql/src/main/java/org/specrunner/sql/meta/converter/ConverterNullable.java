package org.specrunner.sql.meta.converter;

import org.specrunner.sql.meta.IConverter;

public class ConverterNullable implements IConverter {

    public boolean accept(String str) {
        if (str == null) {
            return false;
        }
        str = str.trim();
        if (str.isEmpty()) {
            return false;
        }
        return true;
    }

    public Object convert(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        if (str.isEmpty()) {
            return null;
        }
        return str;
    }
}