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
package org.specrunner.tools.dbms.core;

import org.specrunner.tools.dbms.IPart;

/**
 * Default report part.
 * 
 * @author Thiago Santos
 */
public class PartDefault implements IPart {

    private boolean mandatory;
    private String data;
    private int level;

    public PartDefault(boolean mandatory, String data, int level) {
        this.mandatory = mandatory;
        this.data = data;
        this.level = level;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public boolean hasData() {
        return data != null && !data.isEmpty();
    }

    @Override
    public String getData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append('\t');
        }
        String tmp = data.replace("\n", "\n" + sb + "\t");
        return sb + tmp + "\n";
    }
}
