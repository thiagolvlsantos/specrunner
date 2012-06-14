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
     * Creates a resume.
     * 
     * @param time
     *            The time.
     * @param timestamp
     *            The timestamp.
     * @param input
     *            The input.
     * @param output
     *            The output.
     * @param status
     *            The stattus.
     */
    public Resume(long time, Object timestamp, Object input, Object output, Status status) {
        this.time = time;
        this.timestamp = timestamp;
        this.input = input;
        this.output = output;
        this.status = status;
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
}