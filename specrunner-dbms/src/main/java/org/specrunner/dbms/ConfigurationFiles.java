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
package org.specrunner.dbms;

import java.util.Iterator;

/**
 * Configuration files.
 * 
 * @author Thiago Santos
 */
public class ConfigurationFiles implements Iterable<String> {

    private String[] files;

    public ConfigurationFiles(String... files) {
        this.files = files;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < files.length;
            }

            @Override
            public String next() {
                return files[index++];
            }

            @Override
            public void remove() {
            }
        };
    }
}