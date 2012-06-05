package org.specrunner.report.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * Total processing time.
     */
    protected Long total = 0L;
    /**
     * Time per text.
     */
    protected List<Long> times = new LinkedList<Long>();
    /**
     * Timestamp per test.
     */
    protected List<String> timestamps = new LinkedList<String>();
    /**
     * The input files.
     */
    protected List<Object> inputs = new LinkedList<Object>();
    /**
     * The output files.
     */
    protected List<Object> outputs = new LinkedList<Object>();
    /**
     * The status per file.
     */
    protected List<Status> status = new LinkedList<Status>();

    @Override
    public void analyse(IResultSet result, Map<String, Object> model) {
        Long time = (Long) model.get(PipeTime.TIME);
        total += time;
        times.add(time);
        timestamps.add(String.valueOf(model.get(PipeTimestamp.DATE)));
        inputs.add(model.get(PipeInput.INPUT));
        outputs.add(model.get("output"));
        status.add(result.getStatus());
    }
}
