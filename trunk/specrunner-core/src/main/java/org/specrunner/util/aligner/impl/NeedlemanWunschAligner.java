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
package org.specrunner.util.aligner.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.util.UtilLog;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;

/**
 * Default alignment implementation, a Needleman-Wunsch algorithm.
 * 
 * @author Thiago Santos
 * 
 */
public class NeedlemanWunschAligner implements IStringAligner {

    /**
     * The expected value.
     */
    protected String expected;
    /**
     * The expected value aligned.
     */
    protected StringBuilder expectedAligned;

    /**
     * The character to be using on error report.
     */
    protected char fillCharacter;

    /**
     * The received string.
     */
    protected String received;
    /**
     * The received value aligned.
     */
    protected StringBuilder receivedAligned;

    /**
     * Creates an aligner with default fill character.
     * 
     * @param expected
     *            The expected string.
     * @param received
     *            The received string.
     */
    public NeedlemanWunschAligner(String expected, String received) {
        this(expected, received, IStringAlignerFactory.DEFAULT_FILL_CHARACTER);
    }

    /**
     * Creates an aligner with both strings and fill character.
     * 
     * @param expected
     *            The expected string.
     * @param received
     *            The received string.
     * @param fillCharacter
     *            The fill character.
     */
    public NeedlemanWunschAligner(String expected, String received, char fillCharacter) {
        this.expected = expected;
        this.received = received;
        this.fillCharacter = fillCharacter;
    }

    @Override
    public String getExpected() {
        return expected;
    }

    @Override
    public StringBuilder getExpectedAligned() {
        if (expectedAligned == null) {
            align();
        }
        return expectedAligned;
    }

    @Override
    public char getFillCharacter() {
        return fillCharacter;
    }

    @Override
    public String getReceived() {
        return received;
    }

    @Override
    public StringBuilder getReceivedAligned() {
        if (receivedAligned == null) {
            align();
        }
        return receivedAligned;
    }

    /**
     * Perform alignment.
     */
    protected void align() {
        List<Character> list = alphabet(expected, received);
        int size = list.size();
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("ALFABETH(" + size + ")>" + list);
        }
        int[][] s = scoring(size);
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("SCORING>\n" + toString(s));
        }
        int d = penalty(size);
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("PENALTY>" + d);
        }
        int[][] f = calculate(expected, received, list, s, d);
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("CALCULATE>\n" + toString(f));
        }
        traceback(expected, received, list, s, d, f);
        if (UtilLog.LOG.isTraceEnabled()) {
            UtilLog.LOG.trace("EXPECTED>" + getExpectedAligned());
            UtilLog.LOG.trace("RECEIVED>" + getReceivedAligned());
        }
    }

    /**
     * Creates the local alphabet.
     * 
     * @param a
     *            Left string.
     * @param b
     *            Right string.
     * @return The list of characters.
     */
    protected List<Character> alphabet(String a, String b) {
        List<Character> list = new LinkedList<Character>();
        for (int i = 0; i < a.length(); i++) {
            if (!list.contains(a.charAt(i))) {
                list.add(a.charAt(i));
            }
        }
        for (int i = 0; i < b.length(); i++) {
            if (!list.contains(b.charAt(i))) {
                list.add(b.charAt(i));
            }
        }
        Collections.sort(list);
        return list;
    }

    /**
     * Calculate the matrix of gaps.
     * 
     * @param a
     *            The left string.
     * @param b
     *            The right string.
     * @param list
     *            The alphabet.
     * @param s
     *            The scoring matrix.
     * @param d
     *            The penalty.
     * @return The proximity matrix.
     */
    protected int[][] calculate(String a, String b, List<Character> list, int[][] s, int d) {
        int[][] f = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i < a.length(); i++) {
            f[i][0] = d * i;
        }
        for (int i = 0; i < b.length(); i++) {
            f[0][i] = d * i;
        }
        for (int i = 1; i < a.length() + 1; i++) {
            for (int j = 1; j < b.length() + 1; j++) {
                int match = f[i - 1][j - 1] + s[list.indexOf(a.charAt(i - 1))][list.indexOf(b.charAt(j - 1))];
                int delete = f[i - 1][j] + d;
                int insert = f[i][j - 1] + d;
                f[i][j] = Math.max(match, Math.max(insert, delete));
            }
        }
        return f;
    }

    /**
     * The scoring matrix.
     * 
     * @param size
     *            The size.
     * @return The scoring matrix.
     */
    protected int[][] scoring(int size) {
        int[][] s = new int[size][size];
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[i].length; j++) {
                if (i == j) {
                    s[i][j] = size / 2;
                } else if (i < j) {
                    s[i][j] = s[i][j - 1] - 1;
                } else {
                    s[i][j] = s[i - 1][j] - 1;
                }
            }
        }
        return s;
    }

    /**
     * Calculate penalty.
     * 
     * @param size
     *            The size of alphabet.
     * @return The penalty.
     */
    protected int penalty(int size) {
        return -(size / 2);
    }

    /**
     * Fill the alignment result.
     * 
     * @param a
     *            The left string.
     * @param b
     *            The right string.
     * @param list
     *            The alphabet.
     * @param s
     *            The scoring matrix.
     * @param d
     *            The penalty.
     * @param f
     *            The current proximity matrix.
     */
    protected void traceback(String a, String b, List<Character> list, int[][] s, int d, int[][] f) {
        StringBuilder alignmentA = new StringBuilder();
        StringBuilder alignmentB = new StringBuilder();
        int i = a.length();
        int j = b.length();
        while (i > 0 && j > 0) {
            int score = f[i][j];
            int scoreDiag = f[i - 1][j - 1];
            int scoreUp = f[i][j - 1];
            int scoreLeft = f[i - 1][j];
            if (score == scoreDiag + s[list.indexOf(a.charAt(i - 1))][list.indexOf(b.charAt(j - 1))]) {
                alignmentA.append(a.charAt(--i));
                alignmentB.append(b.charAt(--j));
            } else if (score == scoreLeft + d) {
                alignmentA.append(a.charAt(--i));
                alignmentB.append(fillCharacter);
            } else if (score == scoreUp + d) {
                alignmentA.append(fillCharacter);
                alignmentB.append(b.charAt(--j));
            } else {
                throw new IllegalArgumentException("Fail!");
            }
        }
        while (0 < i) {
            alignmentA.append(a.charAt(--i));
            alignmentB.append(fillCharacter);
        }
        while (0 < j) {
            alignmentA.append(fillCharacter);
            alignmentB.append(b.charAt(--j));
        }
        expectedAligned = alignmentA.reverse();
        receivedAligned = alignmentB.reverse();
    }

    /**
     * String representation of a matrix.
     * 
     * @param d
     *            The matrix.
     * @return The string for the matrix.
     */
    protected String toString(int[][] d) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                sb.append(String.format("%s%4d", j == 0 ? "\t" : ",", d[i][j]));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}