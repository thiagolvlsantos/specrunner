/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
 * Perform base analysis.
 * 
 * @author Thiago Santos
 */
public class BaseAnalyser extends AbstractBaseTool {

    public String analyse(ConnectionInfo current) throws Exception {
        return analyse(current, new ConfigurationFiles("/sr_dbms_tables.properties"), new ConfigurationFiles("/sr_dbms_columns.properties"));
    }

    public String analyse(ConnectionInfo current, ConfigurationFiles fileTableListeners, ConfigurationFiles fileColumnListeners) throws Exception {
        long time = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append("+-------------------- ANALYSER REPORT (" + getDate() + ") ----------------------+");
        sb.append('\n');
        sb.append(process("Database OK.", current, current, fileTableListeners, fileColumnListeners));
        sb.append('\n');
        sb.append("+-------------------------------------------------------------------------------------+");
        sb.append('\n');
        sb.append("TIME: " + (System.currentTimeMillis() - time) + " ms");
        sb.append('\n');
        return sb.toString();
    }
}
