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
package org.specrunner.source.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.specrunner.source.SourceException;
import org.specrunner.source.core.AbstractSourceFactory;
import org.specrunner.util.UtilLog;

/**
 * Loader of Excel files.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class SourceFactoryExcel extends AbstractSourceFactory {

    /**
     * Extension of 2007 Excel files.
     */
    public static final String XLSX = ".xlsx";

    /**
     * Extension of 97-2003 Excel files.
     */
    public static final String XLS = ".xls";

    /**
     * Prefix to be used for specify attributes in cell comments expected to be
     * added to the table definition.
     */
    public static final String TABLE_ATTRIBUTE = "t.";

    /**
     * Prefix to be used for specify attributes in cell comments expected to be
     * added to the caption definition.
     */
    public static final String CAPTION_ATTRIBUTE = "c.";

    /**
     * Prefix to be used for specify attributes in cell comments expected to be
     * added to the row definition.
     */
    public static final String ROW_ATTRIBUTE = "r.";

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
        InputStream in = null;
        POIFSFileSystem fsys = null;
        try {
            Workbook wb = null;
            if (target.trim().toLowerCase().endsWith(XLSX)) {
                pkg = OPCPackage.open(target);
                wb = new XSSFWorkbook(pkg);
            } else {
                in = new FileInputStream(target);
                fsys = new POIFSFileSystem(in);
                wb = new HSSFWorkbook(fsys);
            }
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                Map<String, Dimension> spanMap = new HashMap<String, Dimension>();
                Set<String> ignoreMap = new HashSet<String>();
                for (int j = 0; j < sheet.getNumMergedRegions(); j++) {
                    CellRangeAddress region = sheet.getMergedRegion(j);
                    for (int x = region.getFirstRow(); x <= region.getLastRow(); x++) {
                        for (int y = region.getFirstColumn(); y <= region.getLastColumn(); y++) {
                            if (x == region.getFirstRow() && y == region.getFirstColumn()) {
                                spanMap.put(x + "," + y, new Dimension(region.getLastRow() - x + 1, region.getLastColumn() - y + 1));
                            } else {
                                ignoreMap.add(x + "," + y);
                            }
                        }
                    }
                }
                Element table = new Element("table");
                table.addAttribute(new Attribute("border", "1"));
                html.appendChild(table);
                Element caption = readCaption(table, sheet);
                Iterator<Row> ite = sheet.iterator();
                readBody(table, caption, spanMap, ignoreMap, ite, headers(table, caption, spanMap, ignoreMap, ite));
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
            if (in != null) {
                try {
                    in.close();
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
     * Cell span holder.
     * 
     * @author Thiago Santos
     * 
     */
    private static class Dimension {
        /**
         * Rowspan.
         */
        private int rows;
        /**
         * Colspan.
         */
        private int cols;

        /**
         * Default constructor.
         * 
         * @param rows
         *            Number of rows.
         * @param cols
         *            Number of columns.
         */
        public Dimension(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }
    }

    /**
     * Get caption.
     * 
     * @param table
     *            The target table.
     * @param sheet
     *            The sheet.
     * @return The caption.
     */
    protected Element readCaption(Element table, Sheet sheet) {
        Element caption = new Element("caption");
        table.appendChild(caption);
        {
            caption.appendChild(sheet.getSheetName());
        }
        return caption;
    }

    /**
     * Read header information.
     * 
     * @param table
     *            The target table.
     * @param caption
     *            The caption element.
     * @param spanMap
     *            Map of span cells.
     * @param ignore
     *            Set of cells to ignore.
     * @param ite
     *            The row iterator.
     * @return The number of columns to read.
     */
    protected int headers(Element table, Element caption, Map<String, Dimension> spanMap, Set<String> ignore, Iterator<Row> ite) {
        int result = 0;
        Element thead = new Element("thead");
        table.appendChild(thead);
        {
            Element tr = new Element("tr");
            thead.appendChild(tr);
            {
                if (ite.hasNext()) {
                    Row row = ite.next();
                    result = row.getLastCellNum();
                    for (int i = 0; i < result; i++) {
                        Cell cell = row.getCell(i);
                        if (cell != null) {
                            String key = cell.getRowIndex() + "," + cell.getColumnIndex();
                            if (ignore.contains(key)) {
                                continue;
                            }
                            Element th = new Element("th");
                            tr.appendChild(th);
                            th.appendChild(String.valueOf(extractVal(cell)));
                            addAttributes(table, caption, tr, th, cell, spanMap.get(key));
                        }
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
     * @param caption
     *            The table caption.
     * @param row
     *            The row element.
     * @param item
     *            The header element.
     * @param cell
     *            The cell to read comments from.
     * @param p
     *            The pair, if exist.
     */
    private void addAttributes(Element table, Element caption, Element row, Element item, Cell cell, Dimension p) {
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
                        if (name.startsWith(TABLE_ATTRIBUTE)) {
                            table.addAttribute(new Attribute(name.substring(TABLE_ATTRIBUTE.length(), name.length()), value));
                        } else if (name.startsWith(CAPTION_ATTRIBUTE)) {
                            caption.addAttribute(new Attribute(name.substring(CAPTION_ATTRIBUTE.length(), name.length()), value));
                        } else if (name.startsWith(ROW_ATTRIBUTE)) {
                            row.addAttribute(new Attribute(name.substring(ROW_ATTRIBUTE.length(), name.length()), value));
                        } else {
                            item.addAttribute(new Attribute(name, value));
                        }
                    }
                }
            }
        }
        if (p != null) {
            if (p.rows > 1) {
                item.addAttribute(new Attribute("rowspan", String.valueOf(p.rows)));
            }
            if (p.cols > 1) {
                item.addAttribute(new Attribute("colspan", String.valueOf(p.cols)));
            }
        }
    }

    /**
     * Read content of a table, using the rows iterator and the number of
     * columns.
     * 
     * @param table
     *            The table.
     * @param caption
     *            The table caption.
     * @param spanMap
     *            Map of span cells.
     * @param ignore
     *            Set of cells to ignore.
     * @param ite
     *            The row iterator.
     * @param columns
     *            The number of columns to read.
     */
    protected void readBody(Element table, Element caption, Map<String, Dimension> spanMap, Set<String> ignore, Iterator<Row> ite, int columns) {
        Element tbody = new Element("tbody");
        table.appendChild(tbody);
        {
            while (ite.hasNext()) {
                Element tr = new Element("tr");
                tbody.appendChild(tr);
                {
                    Row row = ite.next();
                    // invalid lines return -1 in row.getFirstCellNum().
                    if (row.getFirstCellNum() < 0) {
                        continue;
                    }
                    for (int k = 0; k < columns; k++) {
                        Cell cell = row.getCell(k);
                        String key = null;
                        if (cell != null) {
                            key = cell.getRowIndex() + "," + cell.getColumnIndex();
                            if (ignore.contains(key)) {
                                continue;
                            }
                        }
                        Element td = new Element("td");
                        tr.appendChild(td);
                        td.appendChild(String.valueOf(extractVal(cell)));
                        if (cell != null) {
                            addAttributes(table, caption, tr, td, cell, spanMap.get(key));
                        }
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