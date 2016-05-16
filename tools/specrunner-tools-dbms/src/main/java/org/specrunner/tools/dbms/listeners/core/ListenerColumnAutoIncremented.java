/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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
package org.specrunner.tools.dbms.listeners.core;

import org.specrunner.tools.dbms.IPart;
import org.specrunner.tools.dbms.Pair;
import org.specrunner.tools.dbms.core.PartDefault;
import org.specrunner.tools.dbms.listeners.IColumnListener;

import schemacrawler.schema.Column;

public class ListenerColumnAutoIncremented implements IColumnListener {

    @Override
    public IPart process(Pair<Column> pair) {
        StringBuilder sb = new StringBuilder();
        switch (pair.getType()) {
        case ADD:
            sb.append("AUTO INCREMENTED is " + pair.getCurrent().isAutoIncremented());
            break;
        case MAINTAIN:
            boolean old = pair.getOld().isAutoIncremented();
            boolean current = pair.getCurrent().isAutoIncremented();
            if (old != current) {
                sb.append("AUTO INCREMENTED is " + old + " should be " + current);
            }
        default:
        }
        return new PartDefault(true, sb.toString(), 3);
    }
}
