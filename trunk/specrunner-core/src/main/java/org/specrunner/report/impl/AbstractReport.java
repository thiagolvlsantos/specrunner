package org.specrunner.report.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.specrunner.impl.pipes.PipeInput;
import org.specrunner.impl.pipes.PipeTime;
import org.specrunner.impl.pipes.PipeTimestamp;
import org.specrunner.report.IReporter;
import org.specrunner.result.IResultSet;

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
     * List of resume of results.
     */
    protected List<Resume> resumes = new LinkedList<Resume>();

    @Override
    public void analyse(IResultSet result, Map<String, Object> model) {
        Resume r = createResume(result, model);
        total += r.getTime();
        resumes.add(r);
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
        return new Resume(index++, time, model.get(PipeTimestamp.DATE), model.get(PipeInput.INPUT), model.get("output"), result.getStatus());
    }

    @Override
    public void report() {
        synchronized (System.out) {
            System.out.println("+-------------------------------- TXT REPORT -------------------------------------+");
            dump("EXECUTION ORDER", resumes);
            Collections.sort(resumes, getComparator());
            dump("PERCENTAGE ORDER", resumes);
            System.out.println("+---------------------------------------------------------------------------------+");
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
                return (int) (o2.getTime() - o1.getTime());
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
