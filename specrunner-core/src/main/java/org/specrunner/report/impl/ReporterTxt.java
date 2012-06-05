package org.specrunner.report.impl;

/**
 * Basic TXT implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ReporterTxt extends AbstractReport {

    /**
     * Divisor.
     */
    private static final int PERCENTAGE = 100;

    @Override
    public void report() {
        synchronized (System.out) {
            System.out.println("+---------------- TXT ---------------------+");
            String pattern = "%10s %10s | %7s | %-24s | %10s | %10s\n";
            System.out.printf(pattern, "", "TIME", "%", " ON", "STATUS", "INPUT <-> OUTPUT");
            pattern = "%10s %10s | %7.2f | %23s  | %10s | %10s\n";
            for (int i = 0; i < times.size(); i++) {
                Long time = times.get(i);
                System.out.printf(pattern, "", time, ((double) time / total) * PERCENTAGE, timestamps.get(i), status.get(i).getName(), inputs.get(i) + " <-> " + outputs.get(i));
            }
            System.out.println("          ----------------------------------");
            pattern = "%10s:%10d\n";
            System.out.printf(pattern, "TOTAL", total);
            System.out.println("+------------------------------------------+");
        }
    }
}