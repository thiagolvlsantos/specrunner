package org.specrunner.report.impl;

import java.util.Comparator;

/**
 * A generic part of a report created by <code>AbstractReport</code>.
 * 
 * @author Thiago Santos
 * 
 */
public class ReportPart {

    /**
     * The header title.
     */
    private String header;
    /**
     * The resume.
     */
    private Comparator<Resume> comparator;

    /**
     * Create a part.
     * 
     * @param header
     *            The title.
     * @param comparator
     *            The comparator.
     */
    public ReportPart(String header, Comparator<Resume> comparator) {
        this.header = header;
        this.comparator = comparator;
    }

    /**
     * Gets the header.
     * 
     * @return The header.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the header.
     * 
     * @param header
     *            The header.
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Gets the comparator.
     * 
     * @return The comparator.
     */
    public Comparator<Resume> getComparator() {
        return comparator;
    }

    /**
     * Sets the header.
     * 
     * @param comparator
     *            The comparator.
     */
    public void setComparator(Comparator<Resume> comparator) {
        this.comparator = comparator;
    }
}
