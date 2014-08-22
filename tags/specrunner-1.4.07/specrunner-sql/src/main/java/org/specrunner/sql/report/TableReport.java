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

import java.util.LinkedList;
import java.util.List;

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
     * The table under analysis.
     */
    private Table table;

    /**
     * List of columns to print.
     */
    private List<Column> columns = new LinkedList<Column>();

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
     */
    public void add(Column column) {
        if (!columns.contains(column)) {
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
        }
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
        int index = 0;
        for (LineReport lr : lines) {
            if (index++ == 0) {
                sb.append("\tDetails:\n\t");
                sb.append(String.format("\t%10s", "Error"));
                int i = 0;
                for (Column c : columns) {
                    sb.append("\t[" + (i++) + "]");
                    sb.append(c.getAlias() + "(" + c.getName() + ")");
                }
                sb.append("\n\n");
            }
            sb.append("\t\t" + lr.asString() + "\n\n");
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
                        th.appendChild("Error");
                    }
                    for (Column c : columns) {
                        th = new Element("th");
                        tr.appendChild(th);
                        {
                            th.addAttribute(new Attribute("class", "sr_lreport"));
                            th.appendChild(c.getAlias());

                            Element sup = new Element("sup");
                            sup.addAttribute(new Attribute("class", "sr_treport"));
                            sup.appendChild(c.getName());
                            th.appendChild(sup);
                        }
                    }
                }
                result.appendChild(lr.asNode());
            }
        }
        return result;
    }
}
