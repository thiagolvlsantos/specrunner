/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.source.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.StringTokenizer;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;

/**
 * Loader of Excel files.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceFactoryExcel extends AbstractSourceFactory {

    @Override
    public boolean accept(Object source) {
        String tmp = source != null ? String.valueOf(source).toLowerCase().trim() : "";
        return tmp.endsWith(".xlsx");
    };

    /**
     * Load a document from a target.
     * 
     * @param uri
     *            The target corresponding uri (if any).
     * @param target
     *            The target.
     * @param encoding
     *            The encoding.
     * @return The document, if exists, null, otherwise.
     * @throws SourceException
     *             On load error.
     */
    @Override
    protected Document fromTarget(URI uri, String target, String encoding) throws SourceException {
        Element html = new Element("html");
        Document result = new Document(html);
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(target);
            Workbook wb = new XSSFWorkbook(pkg);
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                Element table = new Element("table");
                html.appendChild(table);
                readCaption(table, sheet);
                Iterator<Row> ite = sheet.iterator();
                readBody(table, ite, headers(table, ite));
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException(e);
        } finally {
            if (pkg != null) {
                try {
                    pkg.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new SourceException(e);
                }
            }
        }
        return result;
    }

    /**
     * Get caption.
     * 
     * @param table
     *            The target table.
     * @param sheet
     *            The sheet.
     */
    protected void readCaption(Element table, Sheet sheet) {
        Element caption = new Element("caption");
        table.appendChild(caption);
        {
            caption.appendChild(sheet.getSheetName());
        }
    }

    /**
     * Read header information.
     * 
     * @param table
     *            The target table.
     * @param ite
     *            The row iterator.
     * @return The number of columns to read.
     */
    protected int headers(Element table, Iterator<Row> ite) {
        int result = 0;
        Element thead = new Element("thead");
        table.appendChild(thead);
        {
            Element tr = new Element("tr");
            thead.appendChild(tr);
            {
                if (ite.hasNext()) {
                    Row row = ite.next();
                    Cell cell = row.getCell(result);
                    String value = cell != null ? String.valueOf(extractVal(cell)) : null;
                    while (cell != null && value != null) {
                        Element th = new Element("th");
                        tr.appendChild(th);
                        th.appendChild(value);
                        addAttributes(table, tr, th, cell);
                        result++;
                        cell = row.getCell(result);
                        value = cell != null ? String.valueOf(extractVal(cell)) : null;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Add information from cell comments.
     * 
     * @param table
     *            The table element.
     * @param row
     *            The row element.
     * @param th
     *            The header element.
     * @param cell
     *            The cell to read comments from.
     */
    private void addAttributes(Element table, Element row, Element th, Cell cell) {
        Comment c = cell.getCellComment();
        if (c != null) {
            RichTextString rts = c.getString();
            if (rts != null) {
                String text = rts.getString();
                StringTokenizer st = new StringTokenizer(text, "\n");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    int pos = token.indexOf('=');
                    if (pos > 0) {
                        String name = token.substring(0, pos);
                        String value = token.substring(pos + 1).replace("\"", "");
                        if (name.startsWith("t.")) {
                            table.addAttribute(new Attribute(name.substring("t.".length(), name.length()), value));
                        } else if (name.startsWith("r.")) {
                            row.addAttribute(new Attribute(name.substring("r.".length(), name.length()), value));
                        } else {
                            th.addAttribute(new Attribute(name, value));
                        }
                    }
                }
            }
        }
    }

    /**
     * Read content of a table, using the rows iterator and the number of
     * columns.
     * 
     * @param table
     *            The table.
     * @param ite
     *            The row iterator.
     * @param columns
     *            The number of columns to read.
     */
    protected void readBody(Element table, Iterator<Row> ite, int columns) {
        Element tbody = new Element("tbody");
        table.appendChild(tbody);
        {
            while (ite.hasNext()) {
                Element tr = new Element("tr");
                tbody.appendChild(tr);
                {
                    Row row = ite.next();
                    for (int k = 0; k < columns; k++) {
                        Element td = new Element("td");
                        tr.appendChild(td);
                        Cell cell = row.getCell(k);
                        td.appendChild(String.valueOf(extractVal(cell)));
                        addAttributes(table, tr, td, cell);
                    }
                }
            }
        }
    }

    /**
     * Extract the value of a given cell.
     * 
     * @param cell
     *            The cell.
     * @return The corresponding object.
     */
    protected Object extractVal(Cell cell) {
        if (cell == null) {
            return "";
        }
        Object val = null;
        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_BLANK:
            val = null;
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            val = cell.getBooleanCellValue();
            break;
        case Cell.CELL_TYPE_ERROR:
            val = cell.getErrorCellValue();
            break;
        case Cell.CELL_TYPE_FORMULA:
            val = cell.getCellFormula();
            break;
        case Cell.CELL_TYPE_NUMERIC:
            double d = cell.getNumericCellValue();
            String tmp = String.valueOf(d);
            if (tmp.endsWith(".0")) {
                val = tmp.substring(0, tmp.lastIndexOf('.'));
            } else {
                val = d;
            }
            break;
        case Cell.CELL_TYPE_STRING:
            val = cell.getStringCellValue();
            break;
        default:
        }
        return val;
    }
}