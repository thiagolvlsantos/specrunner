package org.specrunner.report.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.specrunner.impl.pipes.PipeTime;
import org.specrunner.report.IReporter;
import org.specrunner.result.IResultSet;

public class ReporterSysout implements IReporter {

    private final List<IResultSet> rs = new LinkedList<IResultSet>();
    private final List<Map<String, Object>> ms = new LinkedList<Map<String, Object>>();

    @Override
    public void analyse(IResultSet result, Map<String, Object> model) {
        rs.add(result);
        ms.add(model);
    }

    @Override
    public void report() {
        if (!rs.isEmpty()) {
            synchronized (System.out) {
                System.out.println("+------------- REPORT -------------+");
                long total = 0;
                int[] colSizes = { 0, 0, 0 };
                for (int i = 0; i < rs.size(); i++) {
                    Map<String, Object> m = ms.get(i);
                    long actual = (Long) m.get(PipeTime.TIME);
                    colSizes[0] = Math.max(colSizes[0], String.valueOf(actual).length());
                    colSizes[1] = Math.max(colSizes[1], rs.get(i).asString().length());
                    total += actual;
                }
                String pattern1 = "%" + (colSizes[0] + 7) + "s %" + colSizes[0] + "s | %5s | %-" + colSizes[1] + "s | %s\n";
                System.out.printf(pattern1, "", "TIME", "%", "RESULT", "FILE NAME");

                String pattern = "%" + (colSizes[0] + 7) + "s %" + colSizes[0] + "d | %2.2f | %-" + colSizes[1] + "s | %s\n";
                for (int i = 0; i < rs.size(); i++) {
                    Map<String, Object> m = ms.get(i);
                    long l = (Long) m.get(PipeTime.TIME);
                    System.out.printf(pattern, "", l, ((double) l / total) * 100, rs.get(i).asString(), m.get("input"));
                }
                System.out.println("----------------------------------------------");
                System.out.printf("%" + (colSizes[0] + 6) + "s: %" + colSizes[0] + "d\n", "TOTAL", total);
                System.out.println("+----------------------------------+");
            }
        }
    }
}
