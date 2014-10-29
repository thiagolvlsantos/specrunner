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

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.specrunner.SRServices;
import org.specrunner.plugins.ActionType;
import org.specrunner.report.core.comparators.IndexComparator;
import org.specrunner.report.core.comparators.StatusComparator;
import org.specrunner.report.core.comparators.TimeComparator;
import org.specrunner.result.Status;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;

/**
 * Basic TXT dumper implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ResumeDumperTxt implements IResumeDumper {

    /**
     * One second in milliseconds.
     */
    private static final int SECOND = 1000;

    /**
     * Parts.
     */
    private List<ReportPart> parts = Arrays.asList(new ReportPart("EXECUTION ORDER", IndexComparator.get()), new ReportPart("PERCENTAGE ORDER", TimeComparator.get()), new ReportPart("STATUS ORDER", StatusComparator.get()));

    @Override
    public List<ReportPart> getParts() {
        return parts;
    }

    @Override
    public Object resume(SRServices services, ResumeReporter parent, boolean finalResume) {
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
        sb.append(String.format(format + "%d", "NUMBER OF TESTS", (parent.getIndex() - 1)));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "%d ms [ %02d:%02d:%02d.%03d (HH:mm:ss.SSS) ]", "TOTAL TIME", parent.getTotal(), TimeUnit.MILLISECONDS.toHours(parent.getTotal()), TimeUnit.MILLISECONDS.toMinutes(parent.getTotal()), TimeUnit.MILLISECONDS.toSeconds(parent.getTotal()), parent.getTotal() % SECOND));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "%7.2f ms", "AVERAGE TIME", (parent.getIndex() > 1 ? (float) parent.getTotal() / (parent.getIndex() - 1) : (float) parent.getTotal())));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "[%s]", "STATUS", status(parent)));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "[%s]", "TYPES", types(parent)));
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
     * @param parent
     *            The parent reporter.
     * @return The overall execution resume.
     */
    public String status(ResumeReporter parent) {
        StringBuilder sb = new StringBuilder();
        for (Entry<Status, Integer> e : parent.getStatus().entrySet()) {
            sb.append(e.getKey().getName() + "=" + e.getValue() + ", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * Global action types as string.
     * 
     * @param parent
     *            The parent reporter.
     * @return The overall execution resume.
     */
    public String types(ResumeReporter parent) {
        StringBuilder sb = new StringBuilder();
        for (Entry<ActionType, Integer> e : parent.getTypes().entrySet()) {
            sb.append(e.getKey().getName() + "=" + e.getValue() + ", ");
        }
        return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
    }

    @Override
    public void dumpStart(SRServices services, ResumeReporter parent) {
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.print("+-------------------------------- TXT REPORT -------------------------------------+\n");
        out.print("+  THREAD: " + services.getThreadName() + "\n");
        out.print("+--------------------------------\n");
    }

    @Override
    public void dumpPart(SRServices services, ResumeReporter parent, String header, List<Resume> list) {
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.printf("  +---------------- TXT (%s)---------------------+%n", header);
        String pattern = "  %10s %10s | %10s | %7s | %-24s | %-18s | %-12s | %10s%n";
        out.printf(pattern, "", "#", "TIME (ms)", "%", "ON", "STATUS", "ASSERTS", "INPUT <-> OUTPUT");
        pattern = "  %10s %10s | %10s | %7.2f | %23s  | %-18s | %-12s | %10s%n";
        for (Resume r : list) {
            out.printf(pattern, "", r.getIndex(), r.getTime(), parent.asPercentage(r.getTime()), r.getTimestamp(), r.getStatus().getName() + " " + r.getStatusCounter() + "/" + r.getStatusTotal(), r.getAssertionCounter() + "/" + r.getAssertionTotal(), r.getInput() + " <-> " + r.getOutput());
        }
        out.print("            ----------------------------------\n");
        pattern = "  %10s %10s : %10d (AVG: %.2f)%n";
        out.printf(pattern, "", "TOTAL", parent.getTotal(), ((double) parent.getTotal() / (list.isEmpty() ? 1 : list.size())));
        out.printf("  +---------------------%s-----------------------+%n", "");
    }

    @Override
    public void dumpResume(SRServices services, ResumeReporter parent, Object resume) {
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.print(resume);
    }

    @Override
    public void dumpEnd(SRServices services, ResumeReporter parent) {
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.print("+---------------------------------------------------------------------------------+\n");
    }
}