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
import org.specrunner.util.UtilSql;
import org.specrunner.util.aligner.core.DefaultAlignmentException;
import org.specrunner.util.xom.IPresentation;
import org.specrunner.util.xom.UtilNode;

/**
 * A line report. Only differences are shown for partial match of registers,
 * alien or missing registers as full described.
 * 
 * @author Thiago Santos
 * 
 */
public class LineReport implements IPresentation {

    /**
     * The line type.
     */
    private RegisterType type;
    /**
     * The parent table.
     */
    private TableReport tableReport;
    /**
     * Mapping from column names to their indexes in table report.
     */
    private Map<String, Integer> columnsToIndexes = new HashMap<String, Integer>();
    /**
     * List of columns.
     */
    private List<Column> columns = new LinkedList<Column>();
    /**
     * List of expected objects.
     */
    private List<Object> expectedObjects = new LinkedList<Object>();
    /**
     * List of received objects.
     */
    private List<Object> receivedObjects = new LinkedList<Object>();

    /**
     * Gets the register type.
     * 
     * @return The type.
     */
    public RegisterType getType() {
        return type;
    }

    /**
     * Set line register type.
     * 
     * @param type
     *            The type.
     */
    public void setType(RegisterType type) {
        this.type = type;
    }

    /**
     * Get parent table report.
     * 
     * @return The table.
     */
    public TableReport getTable() {
        return tableReport;
    }

    /**
     * Set the table report.
     * 
     * @param table
     *            Set the parent report.
     */
    public void setTable(TableReport table) {
        this.tableReport = table;
    }

    /**
     * Get the mapping of columns names to indexes.
     * 
     * @return The mapping.
     */
    public Map<String, Integer> getColumnsToIndexes() {
        return columnsToIndexes;
    }

    /**
     * Set column mapping.
     * 
     * @param columnsToIndexes
     *            A new mapping.
     */
    public void setColumnsToIndexes(Map<String, Integer> columnsToIndexes) {
        this.columnsToIndexes = columnsToIndexes;
    }

    /**
     * Get the list of columns.
     * 
     * @return The columns.
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * Set the column list.
     * 
     * @param columns
     *            The columns.
     */
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * List of expected objects.
     * 
     * @return The list of objects.
     */
    public List<Object> getExpectedObjects() {
        return expectedObjects;
    }

    /**
     * Set the list of expected objects.
     * 
     * @param expectedObjects
     *            The list of expected values.
     */
    public void setExpectedObjects(List<Object> expectedObjects) {
        this.expectedObjects = expectedObjects;
    }

    /**
     * List of received objects.
     * 
     * @return The list of objects.
     */
    public List<Object> getReceivedObjects() {
        return receivedObjects;
    }

    /**
     * Set the list of expected objects.
     * 
     * @param receivedObjects
     *            The expected objects.
     */
    public void setReceivedObjects(List<Object> receivedObjects) {
        this.receivedObjects = receivedObjects;
    }

    /**
     * Default constructor.
     * 
     * @param type
     *            The line type.
     * @param table
     *            The parent table.
     */
    public LineReport(RegisterType type, TableReport table) {
        this.type = type;
        this.tableReport = table;
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.asString() + "|");
        switch (type) {
        case ALIEN:
            for (Column c : tableReport.getTable().getColumns()) {
                Integer index = columnsToIndexes.get(c.getName());
                sb.append(receivedObjects.get(index));
                sb.append('|');
            }
            break;
        case MISSING:
            for (Column c : tableReport.getTable().getColumns()) {
                Integer index = columnsToIndexes.get(c.getName());
                sb.append(expectedObjects.get(index));
                sb.append('|');
            }
            break;
        case DIFFERENT:
            for (Column c : tableReport.getTable().getColumns()) {
                Integer index = columnsToIndexes.get(c.getName());
                if (index != null) {
                    String rec = String.valueOf(receivedObjects.get(index));
                    if (c.isKey()) {
                        sb.append(rec);
                        sb.append('|');
                    } else {
                        String exp = String.valueOf(expectedObjects.get(index));
                        sb.append(new DefaultAlignmentException("", exp, rec).asString() + "|");
                    }
                } else {
                    sb.append('|');
                }
            }
            break;
        default:
            // nothing.
        }
        return sb.toString();
    }

    @Override
    public Node asNode() {
        Element tr = new Element("tr");
        tr.addAttribute(new Attribute("class", type.getStyle() + " sr_lreport"));
        {
            Element td = new Element("td");
            tr.appendChild(td);
            {
                UtilNode.appendCss(td, type.getStyle() + " sr_lreport");
                td.appendChild(type.asNode());
            }
            switch (type) {
            case ALIEN:
                line(tr, receivedObjects);
                break;
            case MISSING:
                line(tr, expectedObjects);
                break;
            case DIFFERENT:
                for (Column c : tableReport.getTable().getColumns()) {
                    Integer index = columnsToIndexes.get(c.getName());
                    td = new Element("td");
                    if (index != null) {
                        UtilNode.appendCss(td, type.getStyle() + " sr_lreport");
                        String rec = String.valueOf(receivedObjects.get(index));
                        Column column = columns.get(index);
                        if (column.isKey()) {
                            td.appendChild(rec);
                        } else {
                            UtilNode.appendCss(td, td.getAttributeValue("class") + " " + type.getStyle() + "_cell");
                            String exp = String.valueOf(expectedObjects.get(index));
                            td.appendChild(new DefaultAlignmentException("", exp, rec).asNode());
                        }
                    }
                    tr.appendChild(td);
                }
                break;
            default:
                // nothing
            }
        }

        return tr;
    }

    /**
     * Dump a line.
     * 
     * @param tr
     *            The element.
     * @param vals
     *            The values to dump.
     */
    protected void line(Element tr, List<Object> vals) {
        for (Column c : tableReport.getTable().getColumns()) {
            Integer index = columnsToIndexes.get(c.getName());
            Element td = new Element("td");
            if (index != null) {
                UtilNode.appendCss(td, type.getStyle());
                td.appendChild(UtilSql.toString(vals.get(index)));
            }
            tr.appendChild(td);
        }
    }

    /**
     * Add register information.
     * 
     * @param c
     *            The column.
     * @param index
     *            The column index.
     * @param expected
     *            The expected value.
     * @param received
     *            The received value.
     */
    public void add(Column c, int index, Object expected, Object received) {
        columnsToIndexes.put(c.getName(), index);
        columns.add(c);
        expectedObjects.add(UtilSql.toString(expected));
        receivedObjects.add(UtilSql.toString(received));
    }

    /**
     * Check if there are some columns in this line report.
     * 
     * @return true, if enable, false, otherwise.
     */
    public boolean isEmpty() {
        return columns.isEmpty();
    }
}