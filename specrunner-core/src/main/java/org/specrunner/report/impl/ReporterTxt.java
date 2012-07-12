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
    protected void dump(String header, List<Resume> list) {
        System.out.printf("\t+---------------- TXT (%s)---------------------+\n", header);
        String pattern = "\t%10s %10s | %10s | %7s | %-24s | %-15s | %10s\n";
        System.out.printf(pattern, "", "#", "TIME (ms)", "%", "ON", "STATUS", "INPUT <-> OUTPUT");
        pattern = "\t%10s %10s | %10s | %7.2f | %23s  | %-15s | %10s\n";
        for (Resume r : list) {
            System.out.printf(pattern, "", r.getIndex(), r.getTime(), asPercentage(r.getTime()), r.getTimestamp(), r.getStatus().getName() + " " + r.getStatusCounter() + "/" + r.getStatusTotal(), r.getInput() + " <-> " + r.getOutput());
        }
        System.out.println("\t          ----------------------------------");
        pattern = "\t%10s %10s : %10d\n";
        System.out.printf(pattern, "", "TOTAL", total);
        System.out.printf("\t+---------------------%s-----------------------+\n", "");
    }
}