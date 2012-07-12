package org.specrunner.report.impl;

import org.specrunner.result.Status;

/**
 * Default resume of a result.
 * 
 * @author Thiago Santos
 * 
 */
public class Resume {

    /**
     * Test index.
     */
    private int index;
    /**
     * Execution time.
     */
    private long time;
    /**
     * Execution timestamp.
     */
    private Object timestamp;
    /**
     * Input information.
     */
    private Object input;
    /**
     * Output information.
     */
    private Object output;
    /**
     * The resulting status.
     */
    private Status status;
    /**
     * The status counter.
     */
    private int statusCounter;
    /**
     * The status total.
     */
    private int statusTotal;

    /**
     * Gets the index.
     * 
     * @return The index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index.
     * 
     * @param index
     *            The index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Get time.
     * 
     * @return The time.
     */
    public long getTime() {
        return time;
    }

    /**
     * Set time.
     * 
     * @param time
     *            The time.
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Get timestamp.
     * 
     * @return The timestamp.
     */
    public Object getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp.
     * 
     * @param timestamp
     *            The timestamp.
     */
    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the input.
     * 
     * @return The input.
     */
    public Object getInput() {
        return input;
    }

    /**
     * Set the input.
     * 
     * @param input
     *            The input.
     */
    public void setInput(Object input) {
        this.input = input;
    }

    /**
     * Get the output.
     * 
     * @return The output.
     */
    public Object getOutput() {
        return output;
    }

    /**
     * Set the output.
     * 
     * @param output
     *            The output.
     */
    public void setOutput(Object output) {
        this.output = output;
    }

    /**
     * Get the status.
     * 
     * @return The status.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * 
     * @param status
     *            The status.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets the status counter.
     * 
     * @return The counter.
     */
    public int getStatusCounter() {
        return statusCounter;
    }

    /**
     * Set the status counter.
     * 
     * @param statusCounter
     *            The counter.
     */
    public void setStatusCounter(int statusCounter) {
        this.statusCounter = statusCounter;
    }

    /**
     * Gets the status total.
     * 
     * @return The total.
     */
    public int getStatusTotal() {
        return statusTotal;
    }

    /**
     * Sets the status total.
     * 
     * @param statusTotal
     *            The total.
     */
    public void setStatusTotal(int statusTotal) {
        this.statusTotal = statusTotal;
    }
}