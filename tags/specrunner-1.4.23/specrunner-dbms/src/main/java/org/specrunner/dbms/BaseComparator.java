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

import java.util.Date;

/**
 * Perform base comparison.
 * 
 * @author Thiago Santos
 */
public class BaseComparator extends AbstractBaseTool {

    public void compare(ConnectionInfo old, ConnectionInfo current, String fileTableListeners, String fileColumnListeners) throws Exception {
        System.out.println("+-------------------- COMPARISON REPORT (" + new Date() + ") --------------------+");
        System.out.println(process("Databases compatible.", old, current, fileTableListeners, fileColumnListeners));
        System.out.println("+------------------------------------------------------------------------------------------------+");
    }
}