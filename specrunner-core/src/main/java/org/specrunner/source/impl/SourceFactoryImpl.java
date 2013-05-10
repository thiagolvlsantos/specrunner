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

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xerces.parsers.AbstractSAXParser;
import org.cyberneko.html.HTMLConfiguration;
import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.source.IDocumentLoader;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * The default implementation. Uses a NekoHTML parser under XOM to read the
 * specification. The use of NekoHTML allows using less rigid XML/HTML documents
 * which are fixed by NekoHTML on reading time.
 * 
 * <p>
 * The settings of NekoHTML makes the XML attributes name be in lower case. This
 * poses a restriction on attribute names of plugins, instead of defining in a
 * <code>IPlugin</code> implementation <code>PluginX</code> an attribute named
 * <code>loadOnStart</code>, use <code>loadonstart</code> in small caps.
 * 
 * @author Thiago Santos
 * 
 */
public class SourceFactoryImpl implements ISourceFactory {

    /**
     * Default encoding.
     */
    public static final String DEFAULT_ENCODING = "ISO-8859-1";

    /**
     * Cache of files.
     */
    private static ThreadLocal<ICache<String, Document>> cache = new ThreadLocal<ICache<String, Document>>() {
        @Override
        protected ICache<String, Document> initialValue() {
            return SpecRunnerServices.get(ICacheFactory.class).newCache(SourceFactoryImpl.class.getName());
        };
    };

    @Override
    public ISource newSource(final InputStream source) throws SourceException {
        return load(source, null);
    }

    @Override
    public ISource newSource(final Reader source) throws SourceException {
        return load(null, source);
    }

    /**
     * Load a source from either an InputStream or a Reader.
     * 
     * @param stream
     *            InputStream.
     * @param reader
     *            Reader.
     * @return The source.
     */
    private ISource load(final InputStream stream, final Reader reader) {
        final Closeable obj = stream != null ? stream : reader;
        return new SourceImpl(null, this, new IDocumentLoader() {
            @Override
            public Document load() throws SourceException {
                Builder builder = getBuilder();
                try {
                    synchronized (builder) {
                        Document build = stream != null ? builder.build(stream) : builder.build(reader);
                        return addDoctype(build);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (obj != null) {
                        try {
                            obj.close();
                        } catch (IOException e1) {
                            if (UtilLog.LOG.isDebugEnabled()) {
                                UtilLog.LOG.debug(e1.getMessage(), e1);
                            }
                        }
                    }
                    if (UtilLog.LOG.isDebugEnabled()) {
                        UtilLog.LOG.debug(e.getMessage(), e);
                    }
                    throw new SourceException("Could not load the '" + (obj != null ? obj.getClass() : null) + "' source '" + obj + "'.", e);
                }
            }
        });
    }

    @Override
    public ISource newSource(String source) throws SourceException {
        String strTmp = source;
        URI uriTmp = null;
        try {
            uriTmp = new URI(strTmp);
            strTmp = uriTmp.toString();
        } catch (URISyntaxException e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        final URI uri = uriTmp;
        final String target = strTmp;
        return new SourceImpl(strTmp, this, new IDocumentLoader() {
            @Override
            public Document load() throws SourceException {
                long time = System.currentTimeMillis();
                Document result = cache.get().get(target);
                if (result == null) {
                    result = fromTarget(uri, target);
                    cache.get().put(target, result);
                }
                result = (Document) result.copy();
                if (UtilLog.LOG.isInfoEnabled()) {
                    UtilLog.LOG.info("Load time " + (System.currentTimeMillis() - time) + " ms for: " + target);
                }
                return result;
            }

        });
    }

    /**
     * Return the XOM document builder.
     * 
     * @return The builder.
     * @throws SourceException
     *             On builder recover error.
     */
    protected Builder getBuilder() throws SourceException {
        try {
            // i've tried to use the same builder, but there is something
            // wrong with NekoHTML parser o reuse, leaving this way for while.
            return new Builder(getParser(), true);
        } catch (Exception e) {
            throw new SourceException(e);
        }
    }

    /**
     * Get the parser.
     * 
     * @return A SaxParser.
     * @throws Exception
     *             On creation error.
     */
    protected AbstractSAXParser getParser() throws Exception {
        AbstractSAXParser parser = new AbstractSAXParser(new HTMLConfiguration()) {
        };
        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        parser.setFeature("http://cyberneko.org/html/features/override-namespaces", false);
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        parser.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
        parser.setProperty("http://cyberneko.org/html/properties/default-encoding", getEncoding());
        return parser;
    }

    /**
     * Get encoding information.
     * 
     * @return The expected encoding of input.
     */
    protected String getEncoding() {
        IFeatureManager fm = SpecRunnerServices.get(IFeatureManager.class);
        String charset = (String) fm.get(ISourceFactory.FEATURE_ENCODING);
        if (charset == null) {
            charset = DEFAULT_ENCODING;
        }
        return charset;
    }

    /**
     * Adds the XHTML Doctype to the document if none is specified.
     * 
     * @param document
     *            The document.
     * @return The document itself.
     */
    protected Document addDoctype(Document document) {
        if (document.getDocType() == null) {
            // <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">
            DocType dt = new DocType("html", "-//W3C//DTD XHTML 1.0 Transitional//EN", "");
            document.insertChild(dt, 0);
        }
        return document;
    }

    /**
     * Load a document from a target.
     * 
     * @param uri
     *            The target corresponding uri (if any).
     * @param target
     *            The target.
     * @return The document, if exists, null, otherwise.
     * @throws SourceException
     *             On load error.
     */
    protected Document fromTarget(URI uri, String target) throws SourceException {
        System.out.println("DOC:" + target);
        if (target.endsWith(".xlsx")) {
            return readExcel(target);
        }
        String encoding = getEncoding();
        Document document = null;
        InputStream fin = null;
        InputStream bin = null;
        try {
            if (uri == null || !target.startsWith("http")) {
                fin = new FileInputStream(cleanTarget(target));
            } else {
                fin = uri.toURL().openStream();
            }
            bin = new BufferedInputStream(fin);
            ISource fromReader = newSource(new InputStreamReader(bin, encoding));
            document = fromReader.getDocument();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Could not load the source '" + target + "'.", e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Unsupported charset '" + encoding + "'.", e);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Invalid URI '" + uri + "'.", e);
        } catch (IOException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Reading exception.", e);
        } catch (SourceException e) {
            e.printStackTrace();
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new SourceException("Could not load the source '" + target + "'.", e);
        } finally {
            if (bin != null) {
                try {
                    bin.close();
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Close file buffered inputstream:" + target);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
            if (fin != null) {
                try {
                    fin.close();
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace("Close file inputstream:" + target);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
        return document;
    }

    private Document readExcel(String target) throws SourceException {
        Element root = new Element("html");
        Document doc = new Document(root);
        try {
            target = target.replace("file:/", "");
            OPCPackage pkg = OPCPackage.open(target);
            Workbook wb = new XSSFWorkbook(pkg);
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet s = wb.getSheetAt(i);
                Element table = new Element("table");
                root.appendChild(table);
                table.addAttribute(new Attribute("class", "prepare"));

                Element caption = new Element("caption");
                table.appendChild(caption);
                caption.appendChild(s.getSheetName());

                Iterator<Row> ite = s.iterator();

                Row r = ite.next();
                int count = 0;
                Cell c = r.getCell(count);
                Element thead = new Element("thead");
                table.appendChild(thead);
                Element tr = new Element("tr");
                thead.appendChild(tr);
                String value = c.getStringCellValue();
                while (c != null && value != null) {
                    Element th = new Element("th");
                    tr.appendChild(th);
                    th.appendChild(value);
                    count++;
                    c = r.getCell(count);
                    if (c != null) {
                        value = c.getStringCellValue();
                    }
                }
                Element tbody = new Element("tbody");
                table.appendChild(tbody);
                while (ite.hasNext()) {
                    tr = new Element("tr");
                    tbody.appendChild(tr);
                    Row row = ite.next();
                    for (int k = 0; k < count; k++) {
                        Element td = new Element("td");
                        tr.appendChild(td);
                        Cell cell = row.getCell(k);
                        if (cell != null) {
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
                            td.appendChild(val != null ? String.valueOf(val) : "");
                        } else {
                            td.appendChild("");
                        }
                    }
                }
            }
            pkg.close();
        } catch (Exception e) {
            throw new SourceException(e);
        }
        return doc;
    }

    /**
     * Clean the target name.
     * 
     * @param target
     *            The target.
     * @return The target normalized.
     */
    protected String cleanTarget(String target) {
        return target == null ? target : target.replace("file:///", "").replace("file://", "").replace("file:/", "");
    }
}