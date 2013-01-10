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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.specrunner.SpecRunnerServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.features.IFeatureManager;
import org.specrunner.impl.pipes.PipeInput;
import org.specrunner.impl.pipes.PipeTime;
import org.specrunner.impl.pipes.PipeTimestamp;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.report.IReporter;
import org.specrunner.result.IResult;
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
     * One second in milliseconds.
     */
    private static final int SECOND = 1000;

    /**
     * Lock to avoid report interference among threads.
     */
    private static final Object LOCK = new Object();

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
    protected Map<Status, Integer> status = new TreeMap<Status, Integer>();

    /**
     * Hash of actions counters.
     */
    protected Map<ActionType, Integer> types = new TreeMap<ActionType, Integer>();

    /**
     * List of resume of results.
     */
    protected List<Resume> resumes = new LinkedList<Resume>();

    /**
     * Feature to set parts of report.
     */
    public static final String FEATURE_PARTS = AbstractReport.class.getName() + ".parts";

    /**
     * Final report parts.
     */
    protected List<ReportPart> parts;

    /**
     * Default behavior.
     */
    public static final List<ReportPart> DEFAULT_PARTS = Arrays.asList(new ReportPart("EXECUTION ORDER", IndexComparator.get()), new ReportPart("PERCENTAGE ORDER", TimeComparator.get()), new ReportPart("STATUS ORDER", StatusComparator.get()));

    /**
     * Get the report pats.
     * 
     * @return The parts.
     */
    public List<ReportPart> getParts() {
        return parts;
    }

    /**
     * Set the parts.
     * 
     * @param parts
     *            The parts.
     */
    public void setParts(List<ReportPart> parts) {
        this.parts = parts;
    }

    /**
     * Prepare feature settings.
     * 
     * @param services
     *            Services.
     */
    protected void setFeatures(SpecRunnerServices services) {
        IFeatureManager fm = services.lookup(IFeatureManager.class);
        parts = null;
        fm.set(FEATURE_PARTS, this);
        if (parts == null) {
            parts = DEFAULT_PARTS;
        }
    }

    @Override
    public void analyse(IResultSet result, Map<String, Object> model) {
        Resume r = createResume(result, model);
        Status s = r.getStatus();
        Integer c = status.get(s);
        if (c == null) {
            c = 0;
        }
        status.put(s, c + 1);
        List<ActionType> listTypes = result.actionTypes();
        for (ActionType at : listTypes) {
            Integer q = types.get(at);
            if (q == null) {
                q = 0;
            }
            types.put(at, q + result.filterByType(at).size());
        }
        total += r.getTime();
        resumes.add(r);
    }

    @Override
    public String resume() {
        String r = resume(false);
        System.out.print(r);
        return r;
    }

    /**
     * Partial resume.
     * 
     * @param finalResume
     *            If it is the final resume.
     * @return The resume.
     */
    protected String resume(boolean finalResume) {
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
        if (!finalResume) {
            header = " STATISTICS (" + SpecRunnerServices.get(IConcurrentMapping.class).getThread() + ") ";
        } else {
            header = " STATISTICS ";
        }
        sb.append(header);
        sb.append(after);
        sb.append("\n");

        String format = "%16s: ";
        sb.append(gap);
        sb.append(String.format(format + "%d", "NUMBER OF TESTS", (index - 1)));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "%d ms", "TOTAL TIME", total));
        sb.append("\n");

        sb.append(gap);
        sb.append(String.format(format + "%02d:%02d:%02d.%03d (HH:mm:ss.SSS)", "FORMATED TIME", TimeUnit.MILLISECONDS.toHours(total), TimeUnit.MILLISECONDS.toMinutes(total), TimeUnit.MILLISECONDS.toSeconds(total), total % SECOND));
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
        Status s = result.getStatus();
        Resume r = createInstance(result, model);
        r.setIndex(index++);
        r.setTime(time);
        r.setTimestamp(model.get(PipeTimestamp.DATE));
        r.setInput(model.get(PipeInput.INPUT));
        r.setOutput(model.get("output"));
        r.setStatus(s);

        List<IResult> byStatus = result.filterByStatus(s);
        r.setStatusCounter(byStatus.size());

        r.setStatusTotal(result.size());

        List<IResult> statusByType = result.filterByType(byStatus, Assertion.INSTANCE);
        r.setAssertionCounter(statusByType.size());

        List<IResult> totalByType = result.filterByType(Assertion.INSTANCE);
        r.setAssertionTotal(totalByType.size());

        return r;
    }

    /**
     * Create a resume instance. Override it to a different report.
     * 
     * @param result
     *            The result set.
     * @param model
     *            The model.
     * @return A new resume instance.
     */
    protected Resume createInstance(IResultSet result, Map<String, Object> model) {
        return new Resume();
    }

    @Override
    public void report(SpecRunnerServices services) {
        if (!resumes.isEmpty()) {
            setFeatures(services);
            synchronized (LOCK) {
                dumpStart();
                for (ReportPart rp : parts) {
                    dumpPart(rp.getHeader(), orderedList(resumes, rp.getComparator()));
                }
                dumpResume(resume(true));
                dumpEnd();
            }
        }
    }

    /**
     * Dump report starting.
     */
    protected abstract void dumpStart();

    /**
     * Creates a copy of the resume list.
     * 
     * @param list
     *            The list to be ordered.
     * @param comparator
     *            The comparator.
     * 
     * @return The list ordered.
     */
    protected List<Resume> orderedList(List<Resume> list, Comparator<Resume> comparator) {
        // create a copy, in case of someone call report each test
        // execution.
        List<Resume> copy = new LinkedList<Resume>(list);
        Collections.sort(copy, comparator);
        return copy;
    }

    /**
     * Dump the resume declaration.
     * 
     * @param header
     *            The header.
     * @param list
     *            The list of resumes.
     */
    protected abstract void dumpPart(String header, List<Resume> list);

    /**
     * Dump resume.
     * 
     * @param resume
     *            Resume information.
     */
    protected abstract void dumpResume(String resume);

    /**
     * Dump report ending.
     */
    protected abstract void dumpEnd();

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
