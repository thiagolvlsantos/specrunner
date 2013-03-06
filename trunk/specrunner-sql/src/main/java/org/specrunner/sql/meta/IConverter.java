package org.specrunner.sql.meta;

public interface IConverter {

    boolean accept(String str);

    Object convert(String str);
}
