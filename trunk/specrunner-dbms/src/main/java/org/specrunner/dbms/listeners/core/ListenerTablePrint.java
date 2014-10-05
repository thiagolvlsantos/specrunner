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

import org.specrunner.dbms.Action;
import org.specrunner.dbms.IPart;
import org.specrunner.dbms.Pair;
import org.specrunner.dbms.core.PartDefault;
import org.specrunner.dbms.listeners.ITableListener;

import schemacrawler.schema.Table;

public class ListenerTablePrint implements ITableListener {

    @Override
    public IPart process(Pair<Table> pair) {
        StringBuilder sb = new StringBuilder();
        sb.append(pair.getType() + " ");
        switch (pair.getType()) {
        case ADD:
            sb.append(pair.getCurrent().getFullName());
            break;
        case REMOVE:
            sb.append(pair.getOld().getFullName());
            break;
        case MAINTAIN:
            sb.append(pair.getOld().getFullName() + " <-> " + pair.getCurrent().getFullName());
            break;
        default:
        }
        return new PartDefault(pair.getType() == Action.REMOVE, sb.toString(), 1);
    }
}
