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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.specrunner.SpecRunnerServices;
import org.specrunner.concurrency.IConcurrentMapping;
import org.specrunner.features.FeatureManagerException;
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
import org.specrunner.util.UtilLog;

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
     */
    protected void setFeatures() {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        parts = null;
        try {
            fm.set(FEATURE_PARTS, "parts", List.class, this);
        } catch (FeatureManagerException e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
        }
        if (parts == null) {
            parts = new LinkedList<ReportPart>();
            parts.add(new ReportPart("EXECUTION ORDER", IndexComparator.get()));
            parts.add(new ReportPart("PERCENTAGE ORDER", TimeComparator.get()));
            parts.add(new ReportPart("STATUS ORDER", StatusComparator.get()));
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
        if (finalResume) {
            gap = "        ";
        } else {
            sb.append(gap);
            sb.append("+----------\n");
        }
        sb.append(gap);
        if (!finalResume) {
            sb.append(String.format(" STATISTICS (" + SpecRunnerServices.get(IConcurrentMapping.class).getThread() + "): #TESTS:%d, STATUS:[%s], TYPES:[%s], TOTAL:%d ms, AVG:%7.2f ms\n", (index - 1), status(), types(), total, (index > 1 ? (float) total / (index - 1) : (float) total)));
            sb.append(gap);
            sb.append("+----------\n");
        } else {
            sb.append(String.format(" STATISTICS : #TESTS:%d, STATUS:[%s], TYPES:[%s]\n", (index - 1), status(), types()));
        }
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
        Resume r = createInstance(result, model);
        r.setIndex(index++);
        r.setTime(time);
        r.setTimestamp(model.get(PipeTimestamp.DATE));
        r.setInput(model.get(PipeInput.INPUT));
        r.setOutput(model.get("output"));
        r.setStatus(status);

        List<IResult> byStatus = result.filterByStatus(status);
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
    public void report() {
        if (!resumes.isEmpty()) {
            setFeatures();
            synchronized (System.out) {
                System.out.println("+-------------------------------- TXT REPORT -------------------------------------+");
                for (ReportPart rp : parts) {
                    dump(rp.getHeader(), orderedList(resumes, rp.getComparator()));
                }
                System.out.print(resume(true));
                System.out.println("+---------------------------------------------------------------------------------+");
            }
        }
    }

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
    protected abstract void dump(String header, List<Resume> list);

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
