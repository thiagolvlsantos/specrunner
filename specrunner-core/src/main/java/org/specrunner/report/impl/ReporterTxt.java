/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.report.impl;

import java.util.List;

/**
 * Basic TXT implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterTxt extends AbstractReport {

    @Override
    protected void dumpStart() {
        System.out.println("+-------------------------------- TXT REPORT -------------------------------------+");
    }

    @Override
    protected void dumpPart(String header, List<Resume> list) {
        System.out.printf("\t+---------------- TXT (%s)---------------------+%n", header);
        String pattern = "\t%10s %10s | %10s | %7s | %-24s | %-15s | %-10s | %10s%n";
        System.out.printf(pattern, "", "#", "TIME (ms)", "%", "ON", "STATUS", "ASSERTS", "INPUT <-> OUTPUT");
        pattern = "\t%10s %10s | %10s | %7.2f | %23s  | %-15s | %-10s | %10s%n";
        for (Resume r : list) {
            System.out.printf(pattern, "", r.getIndex(), r.getTime(), asPercentage(r.getTime()), r.getTimestamp(), r.getStatus().getName() + " " + r.getStatusCounter() + "/" + r.getStatusTotal(), r.getAssertionCounter() + "/" + r.getAssertionTotal(), r.getInput() + " <-> " + r.getOutput());
        }
        System.out.println("\t          ----------------------------------");
        pattern = "\t%10s %10s : %10d (AVG: %.2f)\n";
        System.out.printf(pattern, "", "TOTAL", total, ((double) total / (list.isEmpty() ? 1 : list.size())));
        System.out.printf("\t+---------------------%s-----------------------+%n", "");
    }

    @Override
    protected void dumpResume(String resume) {
        System.out.print(resume);
    }

    @Override
    protected void dumpEnd() {
        System.out.println("+---------------------------------------------------------------------------------+");
    }
}