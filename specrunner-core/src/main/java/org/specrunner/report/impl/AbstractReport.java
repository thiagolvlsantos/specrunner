package org.specrunner.report.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.specrunner.SpecRunnerServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.impl.pipes.PipeInput;
import org.specrunner.impl.pipes.PipeTime;
import org.specrunner.impl.pipes.PipeTimestamp;
import org.specrunner.report.IReporter;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

/**
 * Generic extractor of usefull information for reporter dumps.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractReport implements IReporter {

    /**
     * Divisor.
     */
    protected static final int PERCENTAGE = 100;

    /**
     * Total processing time.
     */
    protected Long total = 0L;

    /**
     * The test index.
     */
    protected int index = 1;

    /**
     * Hash of state counters.
     */
    protected Map<Status, Integer> counter = new TreeMap<Status, Integer>();

    /**
     * List of resume of results.
     */
    protected List<Resume> resumes = new LinkedList<Resume>();

    @Override
    public void analyse(IResultSet result, Map<String, Object> model) {
        Resume r = createResume(result, model);
        Status s = r.getStatus();
        Integer c = counter.get(s);
        if (c == null) {
            c = 0;
        }
        counter.put(s, c + 1);
        total += r.getTime();
        resumes.add(r);
    }

    @Override
    public void resume() {
        System.out.println("+-----");
        System.out.printf(" STATS (" + SpecRunnerServices.get(IConcurrentMapping.class).getThread() + "): #TESTS:%d [%s], TOTAL:%d ms, AVG:%7.2f ms\n", (index - 1), counters(), total, (index > 1 ? (float) total / (index - 1) : (float) total));
        System.out.println("+-----");
    }

    /**
     * Global status counter as string.
     * 
     * @return The overall execution resume.
     */
    protected String counters() {
        StringBuilder sb = new StringBuilder();
        for (Entry<Status, Integer> e : counter.entrySet()) {
            sb.append(e.getKey().getName() + "=" + e.getValue() + ", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    /**
     * Create a resume of result.
     * 
     * @param result
     *            The result.
     * @param model
     *            The model.
     * @return The resume.
     */
    protected Resume createResume(IResultSet result, Map<String, Object> model) {
        Long time = (Long) model.get(PipeTime.TIME);
        Status status = result.getStatus();
        Resume r = new Resume();
        r.setIndex(index++);
        r.setTime(time);
        r.setTimestamp(model.get(PipeTimestamp.DATE));
        r.setInput(model.get(PipeInput.INPUT));
        r.setOutput(model.get("output"));
        r.setStatus(status);
        r.setStatusCounter(result.countStatus(status));
        r.setStatusTotal(result.size());
        return r;
    }

    @Override
    public void report() {
        if (!resumes.isEmpty()) {
            synchronized (System.out) {
                System.out.println("+-------------------------------- TXT REPORT -------------------------------------+");
                dump("EXECUTION ORDER", resumes);
                Collections.sort(resumes, getComparator());
                dump("PERCENTAGE ORDER", resumes);
                System.out.println("+---------------------------------------------------------------------------------+");
            }
        }
    }

    /**
     * Dump the resume declaration.
     * 
     * @param header
     *            The header.
     * @param list
     *            The list of resumes.
     */
    protected abstract void dump(String header, List<Resume> list);

    /**
     * Gets the comparator.
     * 
     * @return The sorting.
     */
    protected Comparator<? super Resume> getComparator() {
        return new Comparator<Resume>() {
            @Override
            public int compare(Resume o1, Resume o2) {
                int result = (int) (o2.getTime() - o1.getTime());
                if (result == 0) {
                    result = o1.getIndex() - o2.getIndex();
                }
                return result;
            }
        };
    }

    /**
     * Returns a time as percentage.
     * 
     * @param time
     *            The time.
     * @return The percentage value.
     */
    protected double asPercentage(Long time) {
        return ((double) time / total) * PERCENTAGE;
    }
}
