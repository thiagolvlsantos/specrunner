/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.core.pipes.PipeInput;
import org.specrunner.core.pipes.PipeTime;
import org.specrunner.core.pipes.PipeTimestamp;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.type.Assertion;
import org.specrunner.report.IReporter;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.result.status.Success;
import org.specrunner.util.output.IOutputFactory;

/**
 * Reporter which accumulates resumes over execution.
 * 
 * @author Thiago Santos
 * 
 */
public class ResumeReporter implements IReporter {

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
    private Long total = 0L;

    /**
     * The test index.
     */
    private int index = 1;

    /**
     * Result status.
     */
    private Status resultStatus = Success.INSTANCE;

    /**
     * Hash of status counters.
     */
    private Map<Status, Integer> status = new TreeMap<Status, Integer>();

    /**
     * Hash of actions counters.
     */
    private Map<ActionType, Integer> types = new TreeMap<ActionType, Integer>();

    /**
     * List of resume of results.
     */
    private List<Resume> resumes = new LinkedList<Resume>();

    /**
     * Feature to set dumpers of report.
     */
    public static final String FEATURE_DUMPERS = ResumeReporter.class.getName() + ".dumpers";
    /**
     * Report dumpers.
     */
    private List<IResumeDumper> dumpers = new LinkedList<IResumeDumper>();

    /**
     * Get total execution time.
     * 
     * @return Total time.
     */
    public Long getTotal() {
        return total;
    }

    /**
     * Get resumes index.
     * 
     * @return Index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get execution status.
     * 
     * @return A status.
     */
    public Status getResultStatus() {
        return resultStatus;
    }

    /**
     * Map of status counts.
     * 
     * @return Map of status to counts.
     */
    public Map<Status, Integer> getStatus() {
        return status;
    }

    /**
     * Map if action types.
     * 
     * @return Map of types.
     */
    public Map<ActionType, Integer> getTypes() {
        return types;
    }

    /**
     * Get resume set.
     * 
     * @return Resume set.
     */
    public List<Resume> getResumes() {
        return resumes;
    }

    /**
     * Get dumpers list.
     * 
     * @return A list.
     */
    public List<IResumeDumper> getDumpers() {
        return dumpers;
    }

    /**
     * Add a dumper of resumes.
     * 
     * @param dumper
     *            A dumper.
     * @return The reporter itself.
     */
    public ResumeReporter add(IResumeDumper dumper) {
        getDumpers().add(dumper);
        return this;
    }

    /**
     * Prepare feature settings.
     * 
     * @param services
     *            Services.
     */
    protected void setFeatures(SRServices services) {
        IFeatureManager fm = services.lookup(IFeatureManager.class);
        fm.set(FEATURE_DUMPERS, this);
    }

    @Override
    public void analyse(IContext context, IResultSet result, Map<String, Object> model) {
        Resume r = newResume(context, result, model);
        Status s = r.getStatus();
        Integer c = status.get(s);
        if (c == null) {
            c = 0;
        }
        status.put(s, c + 1);
        resultStatus = resultStatus.max(s);
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
    public Object partial(SRServices services) {
        List<Object> objs = new LinkedList<Object>();
        for (IResumeDumper rd : dumpers) {
            Object r = rd.resume(services, this, false);
            if (r != null) {
                SRServices.get(IOutputFactory.class).currentOutput().print(r);
            }
            objs.add(r);
        }
        return objs;
    }

    /**
     * Create a resume of result.
     * 
     * @param context
     *            A test context.
     * @param result
     *            The result.
     * @param model
     *            The model.
     * @return The resume.
     */
    protected Resume newResume(IContext context, IResultSet result, Map<String, Object> model) {
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
    public void report(SRServices services) {
        if (!resumes.isEmpty()) {
            setFeatures(services);
            synchronized (LOCK) {
                for (IResumeDumper dumper : dumpers) {
                    dumper.dumpStart(services, this);
                    List<ReportPart> parts = dumper.getParts();
                    if (parts != null) {
                        for (ReportPart rp : parts) {
                            dumper.dumpPart(services, this, rp.getHeader(), orderedList(resumes, rp.getComparator()));
                        }
                    }
                    dumper.dumpResume(services, this, dumper.resume(services, this, true));
                    dumper.dumpEnd(services, this);
                }
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
     * Returns a time as percentage.
     * 
     * @param time
     *            The time.
     * @return The percentage value.
     */
    public double asPercentage(Long time) {
        return ((double) time / total) * PERCENTAGE;
    }
}
