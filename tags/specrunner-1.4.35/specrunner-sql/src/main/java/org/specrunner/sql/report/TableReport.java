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
package org.specrunner.sql.report;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.xom.IPresentation;

/**
 * A table report. Only differences are shown.
 * 
 * @author Thiago Santos
 * 
 */
public class TableReport implements IPresentation {
    /**
     * String flag for keys.
     */
    public static final String KEY = "*";
    /**
     * String flag for reference.
     */
    public static final String REF = "R";
    /**
     * String flag for virtual.
     */
    public static final String VIR = "V";
    /**
     * String flag for sequence.
     */
    public static final String SEQ = "S";
    /**
     * String flag for enumerations.
     */
    public static final String ENUM = "E";
    /**
     * String flag for dates.
     */
    public static final String DATE = "D";

    /**
     * The table under analysis.
     */
    private Table table;

    /**
     * List of columns to print.
     */
    private List<Column> columns = new LinkedList<Column>();

    /**
     * Column sizes.
     */
    private Map<Column, Integer> sizes = new HashMap<Column, Integer>();

    /**
     * Line reports.
     */
    private List<LineReport> lines = new LinkedList<LineReport>();

    /**
     * Constructor.
     * 
     * @param table
     *            Table under analysis.
     */
    public TableReport(Table table) {
        this.table = table;
    }

    /**
     * Get the report table.
     * 
     * @return The report table.
     */
    public Table getTable() {
        return table;
    }

    /**
     * Set the report table.
     * 
     * @param table
     *            The table under analysis.
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Get line reports.
     * 
     * @return The line reports.
     */
    public List<LineReport> getLines() {
        return lines;
    }

    /**
     * Set the line reports.
     * 
     * @param lines
     *            The reports.
     */
    public void setLines(List<LineReport> lines) {
        this.lines = lines;
    }

    /**
     * Get table columns.
     * 
     * @return Columns to report.
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Set columns list.
     * 
     * @param columns
     *            A list.
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * Add a column to report.
     * 
     * @param column
     *            A column.
     * @param size
     *            Suggested size.
     */
    public void add(Column column, Integer size) {
        if (!columns.contains(column)) {
            // move to right position
            int i = 0;
            while (i < columns.size() && columns.get(i).isKey()) {
                i++;
            }
            if (!column.isKey()) {
                while (i < columns.size() && columns.get(i).isReference()) {
                    i++;
                }
            }
            columns.add(i, column);
            String strKey = column.isKey() ? KEY : "";
            String strRef = column.isReference() ? REF : "";
            String strVir = column.isVirtual() ? VIR : "";
            String strSeq = column.isSequence() ? SEQ : "";
            String strEnum = column.isEnum() ? ENUM : "";
            String strDate = column.isDate() ? DATE : "";
            int comp = strKey.length() + strRef.length() + strVir.length() + strSeq.length() + strEnum.length() + strDate.length();
            if (comp > 0) {
                comp += " []".length();
            }
            // adjust column size min to header size
            sizes.put(column, Math.max(column.getAlias().length(), column.getName().length()) + comp + 1);
        }
        sizes.put(column, Math.max(size, sizes.get(column)));
    }

    /**
     * Column size.
     * 
     * @param column
     *            Column definition.
     * @return Size for that column.
     */
    public Integer getSize(Column column) {
        return sizes.get(column);
    }

    /**
     * Set column size.
     * 
     * @param column
     *            Column definition.
     * @param size
     *            New size.
     */
    public void setSize(Column column, Integer size) {
        sizes.put(column, size);
    }

    /**
     * Check if there are line reports.
     * 
     * @return true, if any line report, false, otherwise.
     */
    public boolean isEmpty() {
        return lines.isEmpty();
    }

    /**
     * Add a line report to the table report.
     * 
     * @param lr
     *            A line report.
     */
    public void add(LineReport lr) {
        lines.add(lr);
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append(table.getAlias() + " (" + table.getName() + ")\n");
        StringBuilder tmpFill = new StringBuilder();
        for (Column c : columns) {
            int i = sizes.get(c);
            for (int j = 0; j < i; j++) {
                tmpFill.append(j == 0 ? '+' : '-');
            }
            tmpFill.append("--");
        }
        tmpFill.append('+');
        String fill = tmpFill.toString();
        int index = 0;
        for (LineReport lr : lines) {
            if (index++ == 0) {
                sb.append("\t" + String.format("%10s%s", "+---------", fill) + "\n");
                sb.append(String.format("\t%10s|", ""));
                for (Column c : columns) {
                    sb.append(String.format(" %-" + sizes.get(c) + "s", c.getAlias()));
                    sb.append('|');
                }
                sb.append("\n");
                sb.append(String.format("\t%10s|", "ERROR(S)"));
                for (Column c : columns) {
                    StringBuilder suffix = new StringBuilder();
                    if (c.isKey() || c.isReference() || c.isVirtual() || c.isSequence() || c.isEnum() || c.isDate()) {
                        suffix.append(" [");
                        if (c.isKey()) {
                            suffix.append(KEY);
                        }
                        if (c.isReference()) {
                            suffix.append(REF);
                        }
                        if (c.isVirtual()) {
                            suffix.append(VIR);
                        }
                        if (c.isSequence()) {
                            suffix.append(SEQ);
                        }
                        if (c.isEnum()) {
                            suffix.append(ENUM);
                        }
                        if (c.isDate()) {
                            suffix.append(DATE);
                        }
                        suffix.append("]");
                    }
                    sb.append(String.format(" %-" + sizes.get(c) + "s", c.getName() + suffix));
                    sb.append('|');
                }
                sb.append("\n");
                sb.append("\t" + String.format("%10s%s", "", fill) + "\n");
            }
            sb.append("\t" + lr.asString() + "\n");
            sb.append("\t" + String.format("%10s%s", "", fill) + "\n");
        }
        return sb.toString();
    }

    @Override
    public Node asNode() {
        Element result = new Element("table");
        result.addAttribute(new Attribute("class", "sr_treport"));
        {
            Element cap = new Element("caption");
            {
                cap.addAttribute(new Attribute("class", "sr_treport"));
                cap.appendChild(table.getAlias());

                Element sup = new Element("sup");
                sup.addAttribute(new Attribute("class", "sr_treport"));
                sup.appendChild(table.getName());
                cap.appendChild(sup);
            }
            result.appendChild(cap);
            int index = 0;
            for (LineReport lr : lines) {
                if (index++ == 0) {
                    Element tr = new Element("tr");
                    result.appendChild(tr);
                    tr.addAttribute(new Attribute("class", "sr_lreport"));

                    Element th = new Element("th");
                    tr.appendChild(th);
                    {
                        th.addAttribute(new Attribute("class", "sr_lreport"));
                        th.appendChild("ERROR(S)");
                    }
                    for (Column c : columns) {
                        th = new Element("th");
                        tr.appendChild(th);
                        {
                            th.addAttribute(new Attribute("class", "sr_lreport"));
                            th.appendChild(c.getAlias());

                            th.appendChild(new Element("br"));

                            Element span = new Element("span");
                            span.addAttribute(new Attribute("class", "sr_treport"));
                            span.appendChild(c.getName());
                            if (c.isKey() || c.isReference() || c.isVirtual() || c.isSequence() || c.isEnum() || c.isDate()) {
                                span.appendChild(" [");
                                if (c.isKey()) {
                                    span.appendChild(KEY);
                                }
                                if (c.isReference()) {
                                    span.appendChild(REF);
                                }
                                if (c.isVirtual()) {
                                    span.appendChild(VIR);
                                }
                                if (c.isSequence()) {
                                    span.appendChild(SEQ);
                                }
                                if (c.isEnum()) {
                                    span.appendChild(ENUM);
                                }
                                if (c.isDate()) {
                                    span.appendChild(DATE);
                                }
                                span.appendChild("]");
                            }
                            th.appendChild(span);
                        }
                    }
                }
                result.appendChild(lr.asNode());
            }
        }
        return result;
    }
}
