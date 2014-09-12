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
import org.specrunner.report.core.comparators.IndexComparator;
import org.specrunner.report.core.comparators.StatusComparator;
import org.specrunner.report.core.comparators.TimeComparator;
import org.specrunner.result.IResult;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.result.status.Success;
import org.specrunner.util.output.IOutputFactory;

/**
 * Generic extractor of usefull information for reporter dumps.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractReport implements IReporter {

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
     * Result status.
     */
    protected Status resultStatus = Success.INSTANCE;

    /**
     * Hash of status counters.
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
    protected void setFeatures(SRServices services) {
        IFeatureManager fm = services.lookup(IFeatureManager.class);
        parts = getDefaultParts();
        fm.set(FEATURE_PARTS, this);
    }

    /**
     * Get part of report.
     * 
     * @return A list of parts.
     */
    protected List<ReportPart> getDefaultParts() {
        return DEFAULT_PARTS;
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
        Object r = resume(services, false);
        if (r != null) {
            SRServices.get(IOutputFactory.class).currentOutput().print(r);
        }
        return r;
    }

    /**
     * Partial resume.
     * 
     * @param services
     *            The services instance.
     * @param finalResume
     *            If it is the final resume.
     * @return The resume.
     */
    protected abstract Object resume(SRServices services, boolean finalResume);

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
                dumpStart(services);
                if (parts != null) {
                    for (ReportPart rp : parts) {
                        dumpPart(services, rp.getHeader(), orderedList(resumes, rp.getComparator()));
                    }
                }
                dumpResume(services, resume(services, true));
                dumpEnd(services);
            }
        }
    }

    /**
     * Dump report starting.
     * 
     * @param services
     *            Current instance.
     */
    protected abstract void dumpStart(SRServices services);

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
     * @param services
     *            Current instance.
     * @param header
     *            The header.
     * @param list
     *            The list of resumes.
     */
    protected abstract void dumpPart(SRServices services, String header, List<Resume> list);

    /**
     * Dump resume.
     * 
     * @param services
     *            Current instance.
     * @param resume
     *            Resume information.
     */
    protected abstract void dumpResume(SRServices services, Object resume);

    /**
     * Dump report ending.
     * 
     * @param services
     *            Current instance.
     */
    protected abstract void dumpEnd(SRServices services);

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
