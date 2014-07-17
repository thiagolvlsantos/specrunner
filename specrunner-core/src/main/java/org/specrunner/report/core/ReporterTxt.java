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

import java.util.List;

import org.specrunner.SRServices;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;

/**
 * Basic TXT implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterTxt extends AbstractReport {

    @Override
    protected void dumpStart(SRServices services) {
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.print("+-------------------------------- TXT REPORT -------------------------------------+\n");
        out.print("+  THREAD: " + services.getThreadName() + "\n");
        out.print("+--------------------------------\n");
    }

    @Override
    protected void dumpPart(SRServices services, String header, List<Resume> list) {
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.printf("  +---------------- TXT (%s)---------------------+%n", header);
        String pattern = "  %10s %10s | %10s | %7s | %-24s | %-17s | %-12s | %10s%n";
        out.printf(pattern, "", "#", "TIME (ms)", "%", "ON", "STATUS", "ASSERTS", "INPUT <-> OUTPUT");
        pattern = "  %10s %10s | %10s | %7.2f | %23s  | %-17s | %-12s | %10s%n";
        for (Resume r : list) {
            out.printf(pattern, "", r.getIndex(), r.getTime(), asPercentage(r.getTime()), r.getTimestamp(), r.getStatus().getName() + " " + r.getStatusCounter() + "/" + r.getStatusTotal(), r.getAssertionCounter() + "/" + r.getAssertionTotal(), r.getInput() + " <-> " + r.getOutput());
        }
        out.print("            ----------------------------------\n");
        pattern = "  %10s %10s : %10d (AVG: %.2f)%n";
        out.printf(pattern, "", "TOTAL", total, ((double) total / (list.isEmpty() ? 1 : list.size())));
        out.printf("  +---------------------%s-----------------------+%n", "");
    }

    @Override
    protected void dumpResume(SRServices services, String resume) {
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.print(resume);
    }

    @Override
    protected void dumpEnd(SRServices services) {
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.print("+---------------------------------------------------------------------------------+\n");
    }
}