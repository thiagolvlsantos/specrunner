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
package org.specrunner.tools.dbms;

/**
 * Perform base comparison.
 * 
 * @author Thiago Santos
 */
public class BaseComparator extends AbstractBaseTool {

    public void compare(ConnectionInfo old, ConnectionInfo current) throws Exception {
        compare(old, current, new ConfigurationFiles("/sr_dbms_tables.properties"), new ConfigurationFiles("/sr_dbms_columns.properties"));
    }

    public void compare(ConnectionInfo old, ConnectionInfo current, ConfigurationFiles fileTableListeners, ConfigurationFiles fileColumnListeners) throws Exception {
        long time = System.currentTimeMillis();
        System.out.println("+-------------------- COMPARISON REPORT (" + getDate() + ") --------------------+");
        System.out.println(process("Databases compatible.", old, current, fileTableListeners, fileColumnListeners));
        System.out.println("+-------------------------------------------------------------------------------------+");
        System.out.println("TIME: " + (System.currentTimeMillis() - time) + " ms");
    }
}