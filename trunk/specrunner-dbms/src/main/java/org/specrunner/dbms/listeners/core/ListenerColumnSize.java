/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2014  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.specrunner.dbms.listeners.core;

import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerColumnSize implements IColumnListener {

    @Override
    public IPart process(Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case ADD:
            int add = pair.getCurrent().getColumnDataType().getJavaSqlType().getJavaSqlType();
            if (add == java.sql.Types.CHAR || add == java.sql.Types.VARCHAR) {
                sb.append("SIZE is " + pair.getCurrent().getSize());
            }
            break;
        case MAINTAIN:
            int type = pair.getOld().getColumnDataType().getJavaSqlType().getJavaSqlType();
            if (type == java.sql.Types.CHAR || type == java.sql.Types.VARCHAR) {
                int old = pair.getOld().getSize();
                int current = pair.getCurrent().getSize();
                if (old != current) {
                    sb.append("SIZE is " + old + " should be " + current);
                }
            }
        default:
        }
        return new PartDefault(true, sb.toString(), 3);
    }
}
