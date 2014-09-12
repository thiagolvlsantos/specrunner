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
package org.specrunner.report.core;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.specrunner.SRServices;
import org.specrunner.plugins.ActionType;
import org.specrunner.result.Status;

/**
 * Generic extractor of usefull information for reporter dumps.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractTxtReport extends AbstractReport {

    /**
     * One second in milliseconds.
     */
    private static final int SECOND = 1000;

    @Override
    protected Object resume(SRServices services, boolean finalResume) {
        StringBuilder sb = new StringBuilder();
        String gap = "";
        String before = "+------";
        String after = "------+";
        if (finalResume) {
            gap = "        ";
        }
        sb.append(gap);
        sb.append(before);
        String header = null;
        header = " STATISTICS (" + services.getThreadName() + ") ";
        sb.append(header);
        sb.append(after);
        sb.append("\n");

        String format = "%16s: ";
        sb.append(gap);
        sb.append(String.format(format + "%d", "NUMBER OF TESTS", (index - 1)));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "%d ms [ %02d:%02d:%02d.%03d (HH:mm:ss.SSS) ]", "TOTAL TIME", total, TimeUnit.MILLISECONDS.toHours(total), TimeUnit.MILLISECONDS.toMinutes(total), TimeUnit.MILLISECONDS.toSeconds(total), total % SECOND));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "%7.2f ms", "AVERAGE TIME", (index > 1 ? (float) total / (index - 1) : (float) total)));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "[%s]", "STATUS", status()));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "[%s]", "TYPES", types()));
        sb.append("\n");

        sb.append(gap);
        sb.append(before);
        for (int i = 0; i < header.length(); i++) {
            sb.append("-");
        }
        sb.append(after);
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Global status as string.
     * 
     * @return The overall execution resume.
     */
    protected String status() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Status, Integer> e : status.entrySet()) {
            sb.append(e.getKey().getName() + "=" + e.getValue() + ", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * Global action types as string.
     * 
     * @return The overall execution resume.
     */
    protected String types() {
        StringBuilder sb = new StringBuilder();
        for (Entry<ActionType, Integer> e : types.entrySet()) {
            sb.append(e.getKey().getName() + "=" + e.getValue() + ", ");
        }
        return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
    }
}