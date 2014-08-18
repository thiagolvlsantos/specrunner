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
     * The assertion counter.
     */
    private int assertionCounter;
    /**
     * The assertion total.
     */
    private int assertionTotal;

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

    /**
     * Gets the assertions counter.
     * 
     * @return The assertions counter.
     */
    public int getAssertionCounter() {
        return assertionCounter;
    }

    /**
     * Sets the assertions counter.
     * 
     * @param assertionCounter
     *            The assertions counter.
     */
    public void setAssertionCounter(int assertionCounter) {
        this.assertionCounter = assertionCounter;
    }

    /**
     * Gets the assertions total counter.
     * 
     * @return The assertions total counter.
     */
    public int getAssertionTotal() {
        return assertionTotal;
    }

    /**
     * Sets the assertions total.
     * 
     * @param assertionTotal
     *            The assertions total counter.
     */
    public void setAssertionTotal(int assertionTotal) {
        this.assertionTotal = assertionTotal;
    }
}