package org.specrunner.util.math;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Range of integers.
 * 
 * @author Thiago Santos
 */
public final class Range {

    /**
     * Undefined range.
     */
    public static final String RANGE = "...";
    /**
     * Start value.
     */
    private int start;
    /**
     * End value.
     */
    private int end;

    /**
     * Default constructor.
     * 
     * @param start
     *            Start value.
     * @param end
     *            End value.
     */
    private Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Check if value belongs to a range.
     * 
     * @param index
     *            Value.
     * @return true, if belongs to range, false, otherwise.
     */
    public boolean between(int index) {
        return (start <= index) && (index <= end);
    }

    /**
     * A valid range has start lower than end.
     * 
     * @return true, if valid, false, otherwise.
     */
    public boolean valid() {
        return start <= end;
    }

    /**
     * Get a range.
     * 
     * @param range
     *            String representation.
     * @param min
     *            Min value.
     * @param max
     *            Max value.
     * @return A range, if valid, null, otherwise.
     */
    public static Range getRange(String range, int min, int max) {
        Range result = null;
        int pos = range.indexOf('-');
        if (pos < 0) {
            if (range.startsWith(RANGE)) {
                result = new Range(min, Integer.parseInt(range.substring(RANGE.length())));
            } else if (range.endsWith(RANGE)) {
                result = new Range(Integer.parseInt(range.substring(0, range.length() - RANGE.length())), max);
            } else {
                int p = Integer.parseInt(range);
                result = new Range(p, p);
            }
        } else {
            result = new Range(Integer.parseInt(range.substring(0, pos)), Integer.parseInt(range.substring(pos + 1)));
        }
        if (!result.valid()) {
            throw new IllegalArgumentException("Invalid range: " + result);
        }
        return result;
    }

    /**
     * Range list creator.
     * 
     * @param ranges
     *            Representation.
     * @param separator
     *            Token separator.
     * @param min
     *            Min value.
     * @param max
     *            Max value.
     * @return A list of ranges.
     */
    public static List<Range> getRanges(String ranges, String separator, int min, int max) {
        if (ranges == null) {
            return Arrays.asList(new Range(min, max));
        }
        if (separator == null) {
            separator = ";";
        }
        List<Range> result = new LinkedList<Range>();
        StringTokenizer st = new StringTokenizer(ranges, separator);
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            Range r = getRange(str, min, max);
            if (r != null) {
                result.add(r);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Range[" + start + "<= n <=" + end + "]";
    }
}
