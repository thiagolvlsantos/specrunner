/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

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

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverterReverse;
import org.specrunner.expressions.EMode;
import org.specrunner.expressions.INullEmptyHandler;
import org.specrunner.sql.meta.Column;
import org.specrunner.util.UtilLog;
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
     * Null handler reference.
     */
    private INullEmptyHandler nullEmptyHandler;
    /**
     * Mapping from column names to their indexes in table report.
     */
    private Map<String, Integer> columnsToIndexes = new HashMap<String, Integer>();
    /**
     * List of expected objects.
     */
    private List<String> expectedObjects = new LinkedList<String>();
    /**
     * List of received objects.
     */
    private List<String> receivedObjects = new LinkedList<String>();

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
     * List of expected objects.
     * 
     * @return The list of objects.
     */
    public List<String> getExpectedObjects() {
        return expectedObjects;
    }

    /**
     * Set the list of expected objects.
     * 
     * @param expectedObjects
     *            The list of expected values.
     */
    public void setExpectedObjects(List<String> expectedObjects) {
        this.expectedObjects = expectedObjects;
    }

    /**
     * List of received objects.
     * 
     * @return The list of objects.
     */
    public List<String> getReceivedObjects() {
        return receivedObjects;
    }

    /**
     * Set the list of expected objects.
     * 
     * @param receivedObjects
     *            The expected objects.
     */
    public void setReceivedObjects(List<String> receivedObjects) {
        this.receivedObjects = receivedObjects;
    }

    /**
     * Default constructor.
     * 
     * @param type
     *            The line type.
     * @param table
     *            The parent table.
     * @param nullEmptyHandler
     *            A handler.
     */
    public LineReport(RegisterType type, TableReport table, INullEmptyHandler nullEmptyHandler) {
        this.type = type;
        this.tableReport = table;
        this.nullEmptyHandler = nullEmptyHandler;
    }

    @Override
    public String asString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%10s|", type.asString()));
        switch (type) {
        case EXTRA:
            line(sb, receivedObjects);
            break;
        case MISSING:
            line(sb, expectedObjects);
            break;
        case DIFFERENT:
            for (Column c : tableReport.getColumns()) {
                Integer colsize = tableReport.getSize(c);
                Integer index = columnsToIndexes.get(c.getName());
                if (index != null) {
                    if (c.isKey()) {
                        String rec = receivedObjects.get(index);
                        sb.append(String.format(" %-" + colsize + "s", nullOrEmpty(c, rec)));
                    } else {
                        String exp = expectedObjects.get(index);
                        sb.append(String.format(" EXP:%-" + (colsize - "EXP:".length()) + "s", nullOrEmptyWrapp(c, exp)));
                    }
                } else {
                    sb.append(String.format(" %-" + colsize + "s", ""));
                }
                sb.append('|');
            }
            break;
        default:
            // nothing.
        }
        if (type == RegisterType.DIFFERENT) {
            sb.append("\n\t");
            sb.append(String.format("%10s|", ""));
            for (Column c : tableReport.getColumns()) {
                Integer colsize = tableReport.getSize(c);
                Integer index = columnsToIndexes.get(c.getName());
                if (index != null) {
                    if (!c.isKey()) {
                        String rec = receivedObjects.get(index);
                        sb.append(String.format(" REC:%-" + (colsize - "REC:".length()) + "s", nullOrEmptyWrapp(c, rec)));
                    } else {
                        sb.append(String.format(" %-" + colsize + "s", ""));
                    }
                } else {
                    sb.append(String.format(" %-" + colsize + "s", ""));
                }
                sb.append('|');
            }
        }
        return sb.toString();
    }

    /**
     * Print a line.
     * 
     * @param sb
     *            String representation.
     * @param list
     *            List of object.
     */
    protected void line(StringBuilder sb, List<String> list) {
        for (Column c : tableReport.getColumns()) {
            Integer index = columnsToIndexes.get(c.getName());
            if (index != null) {
                sb.append(String.format(" %-" + tableReport.getSize(c) + "s", c.isKey() ? nullOrEmpty(c, list.get(index)) : nullOrEmptyWrapp(c, list.get(index))));
            } else {
                sb.append(String.format(" %-" + tableReport.getSize(c) + "s", nullOrEmpty(c, null)));
            }
            sb.append('|');
        }
    }

    /**
     * As empty or null string.
     * 
     * @param c
     *            Column.
     * @param exp
     *            Value.
     * @return Value.
     */
    public String nullOrEmpty(Column c, String exp) {
        String out = value(c, exp);
        return out == null ? nullEmptyHandler.nullValue(EMode.COMPARE) : (out.isEmpty() ? nullEmptyHandler.emptyValue(EMode.COMPARE) : out);
    }

    /**
     * Recover better representation for a value.
     * 
     * @param c
     *            A column.
     * @param exp
     *            A expression.
     * @return String representation.
     */
    protected String value(Column c, String exp) {
        String out = exp;
        if (c.getConverter() instanceof IConverterReverse) {
            IConverterReverse converter = (IConverterReverse) c.getConverter();
            List<String> arguments = c.getArguments();
            try {
                Object tmp = converter.revert(exp, arguments.toArray(new Object[arguments.size()]));
                out = tmp == null ? null : String.valueOf(tmp);
            } catch (ConverterException e) {
                if (UtilLog.LOG.isTraceEnabled()) {
                    UtilLog.LOG.trace("Unable to revert '" + exp + "' with: " + converter + "" + arguments);
                }
            }
        }
        return out;
    }

    /**
     * Wrapped text.
     *
     * @param c
     *            Column.
     * @param exp
     *            Value.
     * @return Value.
     */
    public String nullOrEmptyWrapp(Column c, String exp) {
        String out = value(c, exp);
        return out == null ? nullEmptyHandler.nullValue(EMode.COMPARE) : (out.isEmpty() ? nullEmptyHandler.emptyValue(EMode.COMPARE) : (out.equals(exp) ? "'" + out + "'" : out));
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
            case EXTRA:
                line(tr, receivedObjects);
                break;
            case MISSING:
                line(tr, expectedObjects);
                break;
            case DIFFERENT:
                for (Column c : tableReport.getColumns()) {
                    Integer index = columnsToIndexes.get(c.getName());
                    td = new Element("td");
                    if (index != null) {
                        UtilNode.appendCss(td, type.getStyle() + " sr_lreport");
                        String rec = receivedObjects.get(index);
                        if (c.isKey()) {
                            td.appendChild(nullOrEmpty(c, rec));
                        } else {
                            UtilNode.appendCss(td, td.getAttributeValue("class") + " " + type.getStyle() + "_cell");
                            String exp = expectedObjects.get(index);
                            td.appendChild(new DefaultAlignmentException("", nullOrEmpty(c, exp), nullOrEmpty(c, rec)).asNode());
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
    protected void line(Element tr, List<String> vals) {
        for (Column c : tableReport.getColumns()) {
            Integer index = columnsToIndexes.get(c.getName());
            Element td = new Element("td");
            if (index != null) {
                UtilNode.appendCss(td, type.getStyle());
                td.appendChild(c.isKey() ? nullOrEmpty(c, vals.get(index)) : nullOrEmptyWrapp(c, vals.get(index)));
            } else {
                td.appendChild(nullOrEmpty(c, null));
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
        String strExp = UtilSql.toStringNullable(expected);
        String strRec = UtilSql.toStringNullable(received);
        int sizeMax = 0;
        if (strExp == null || strRec == null) {
            String nullValue = nullEmptyHandler.nullValue(EMode.COMPARE);
            String emptyValue = nullEmptyHandler.emptyValue(EMode.COMPARE);
            sizeMax = Math.max(String.valueOf(nullValue).length(), String.valueOf(emptyValue).length());
        }
        String strExpInv = nullOrEmptyWrapp(c, strExp);
        String strRecInv = nullOrEmptyWrapp(c, strRec);
        sizeMax = Math.max(strExp != null ? strExp.length() : sizeMax, strRec != null ? strRec.length() : sizeMax);
        sizeMax = Math.max(strExpInv != null ? strExpInv.length() : sizeMax, strRecInv != null ? strRecInv.length() : sizeMax);
        tableReport.add(c, type == RegisterType.DIFFERENT ? sizeMax + "EXP:".length() : sizeMax);
        expectedObjects.add(strExp);
        receivedObjects.add(strRec);
    }

    /**
     * Check if there are some columns in this line report.
     * 
     * @return true, if enable, false, otherwise.
     */
    public boolean isEmpty() {
        return columnsToIndexes.isEmpty();
    }
}
