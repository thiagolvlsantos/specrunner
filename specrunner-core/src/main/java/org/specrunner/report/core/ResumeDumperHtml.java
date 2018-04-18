/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.report.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.specrunner.SRServices;
import org.specrunner.dumper.core.ConstantsDumperFile;
import org.specrunner.features.IFeatureManager;
import org.specrunner.report.core.comparators.IndexComparator;
import org.specrunner.report.core.comparators.StatusComparator;
import org.specrunner.report.core.comparators.TimeComparator;
import org.specrunner.source.core.UtilEncoding;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.util.UtilIO;
import org.specrunner.util.UtilLog;
import org.specrunner.util.output.IOutput;
import org.specrunner.util.output.IOutputFactory;
import org.specrunner.util.resources.ResourceFinder;
import org.specrunner.util.string.IStringNormalizer;

import nu.xom.Attribute;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

/**
 * Basic HTML dumper implementation.
 * 
 * @author Thiago Santos
 * 
 */
public class ResumeDumperHtml implements IResumeDumper {
    /**
     * Base document.
     */
    private Document doc;
    /**
     * Root element.
     */
    private Element html;
    /**
     * Header part.
     */
    private Element head;
    /**
     * Content part.
     */
    private Element body;
    /**
     * Output directory.
     */
    private File output;
    /**
     * Output file.
     */
    private File outputName;
    /**
     * Current date.
     */
    private String date;

    /**
     * Report parts.
     */
    private List<ReportPart> parts = Arrays.asList(new ReportPart("Status order", StatusComparator.get()), new ReportPart("Time order", TimeComparator.get()), new ReportPart("Execution order", IndexComparator.get()));

    @Override
    public List<ReportPart> getParts() {
        return parts;
    }

    @Override
    public void dumpStart(SRServices services, ResumeReporter parent) {
        html = new Element("html");
        doc = new Document(html);
        doc.setDocType(new DocType("html"));
        {
            head = new Element("head");
            html.appendChild(head);
        }
        {
            body = new Element("head");
            html.appendChild(body);
            {
                Element h1 = new Element("h1");
                h1.addAttribute(new Attribute("class", "htmlreport " + parent.getResultStatus().getCssName()));
                h1.appendChild("HTML Report " + String.format(" [%s (%d of %d) | %s | AVG: %.2f ms | TOTAL:%d ms]", parent.getResultStatus().getName(), parent.getStatus().get(parent.getResultStatus()), parent.getResumes().size(), services.getThreadName(), ((double) parent.getTotal() / (parent.getResumes().isEmpty() ? 1 : parent.getResumes().size())), parent.getTotal()));
                body.appendChild(h1);
                date = new DateTime().toString("HH:mm:ss.SSS MM/dd/yyyy");
            }
        }
        output = ConstantsDumperFile.DEFAULT_OUTPUT_DIRECTORY;
        String suffix = services.getThreadName().equals("main") ? "" : "_" + services.getThreadName();
        outputName = new File(output, "result" + suffix + ".html");
    }

    @Override
    public Object resume(SRServices services, ResumeReporter parent, boolean finalResume) {
        return null;
    }

    @Override
    public void dumpResume(SRServices services, ResumeReporter parent, Object resume) {
        // useless
    }

    @Override
    public void dumpPart(SRServices services, ResumeReporter parent, String header, List<Resume> list) {
        Element quote = new Element("blockquote");
        body.appendChild(quote);
        {
            Element table = new Element("table");
            table.addAttribute(new Attribute("class", "htmlreport"));
            quote.appendChild(table);

            IStringNormalizer normalizer = services.lookup(IStringNormalizer.class);
            String id = normalizer.normalize(header);
            {
                Element caption = new Element("caption");
                Element button = new Element("input");
                button.addAttribute(new Attribute("type", "button"));
                button.addAttribute(new Attribute("class", "htmlreport"));
                button.addAttribute(new Attribute("id", id));
                button.addAttribute(new Attribute("value", "-"));
                caption.appendChild(button);
                caption.appendChild("  " + header + " at " + date);
                table.appendChild(caption);
            }

            Element tbody = new Element("tbody");
            tbody.addAttribute(new Attribute("id", "body" + id));
            table.appendChild(tbody);

            Element tr = new Element("tr");
            tbody.appendChild(tr);
            {
                for (String s : Arrays.asList("#", "TIME (ms)", "%", "ON", "STATUS", "ASSERTS", "OUTPUT")) {
                    Element th = new Element("th");
                    th.appendChild(s);
                    tr.appendChild(th);
                }
            }

            for (Resume r : list) {
                tr = new Element("tr");
                tr.addAttribute(new Attribute("class", r.getStatus().getCssName()));
                tbody.appendChild(tr);
                {
                    Element td = new Element("td");
                    tr.appendChild(td);
                    td.appendChild(String.valueOf(r.getIndex()));

                    td = new Element("td");
                    tr.appendChild(td);
                    td.appendChild(String.valueOf(r.getTime()));

                    td = new Element("td");
                    tr.appendChild(td);
                    td.appendChild(String.format("%7.2f", parent.asPercentage(r.getTime())));

                    td = new Element("td");
                    tr.appendChild(td);
                    td.appendChild(String.valueOf(r.getTimestamp()));

                    td = new Element("td");
                    tr.appendChild(td);
                    td.appendChild(r.getStatus().getName() + " " + r.getStatusCounter() + "/" + r.getStatusTotal());

                    td = new Element("td");
                    tr.appendChild(td);
                    td.appendChild(r.getAssertionCounter() + "/" + r.getAssertionTotal());

                    td = new Element("td");
                    tr.appendChild(td);
                    {
                        Element a = new Element("a");
                        td.appendChild(a);

                        Object obj = r.getOutput();
                        String str = String.valueOf(obj);
                        File file = new File(str);
                        String relative = relative(outputName.getAbsolutePath(), file.getAbsolutePath());
                        a.addAttribute(new Attribute("href", relative));
                        td.appendChild(obj instanceof File ? ((File) obj).getName() : str.replace(ConstantsDumperFile.DEFAULT_OUTPUT_DIRECTORY.getAbsolutePath() + File.separator, ""));
                    }
                }
            }
        }
    }

    /**
     * Relative path.
     * 
     * @param base
     *            Base path.
     * @param target
     *            Target.
     * @return Relative link.
     */
    public String relative(String base, String target) {
        int left = base.indexOf(File.separatorChar);
        int right = target.indexOf(File.separatorChar);
        if (left >= 0 && right >= 0 && base.substring(0, left).equals(target.substring(0, right))) {
            return relative(base.substring(left + 1), target.substring(right + 1));
        }
        StringBuilder sb = new StringBuilder();
        while (left >= 0) {
            sb.append(".." + File.separatorChar);
            left = base.indexOf(File.separatorChar, left + 1);
        }
        sb.append(target);
        return sb.toString();
    }

    @Override
    public void dumpEnd(SRServices services, ResumeReporter parent) {
        appendResources(services, output);
        write(services, doc, outputName);
        IOutput out = services.lookup(IOutputFactory.class).currentOutput();
        out.println(services.getThreadName() + ": HTML resume at " + outputName.getAbsolutePath());
    }

    /**
     * Append resources to output, and write data to disk.
     * 
     * @param services
     *            Services base.
     * @param dir
     *            Output directory.
     */
    public void appendResources(SRServices services, File dir) {
        try {
            List<URL> all = new LinkedList<URL>();
            for (String s : Arrays.asList("css/" + IResourceManager.DEFAULT_CSS, "js/jquery.js", "js/" + IResourceManager.DEFAULT_JS)) {
                all.addAll(services.lookup(ResourceFinder.class).getAllResources(s));
            }
            for (URL url : all) {
                String tmp = url.toString();
                File out = new File(dir, tmp.substring(tmp.lastIndexOf('/')));
                UtilIO.writeToClose(url, out);
                String link = out.getName().replace(File.separator, "/");
                if (url.getFile().endsWith("css")) {
                    Element css = new Element("link");
                    css.addAttribute(new Attribute("rel", "stylesheet"));
                    css.addAttribute(new Attribute("type", "text/css"));
                    css.addAttribute(new Attribute("href", link));
                    head.appendChild(css);
                } else if (url.getFile().endsWith("js")) {
                    Element js = new Element("script");
                    js.addAttribute(new Attribute("type", "text/javascript"));
                    js.addAttribute(new Attribute("src", link));
                    js.appendChild("<!-- comment -->");
                    head.appendChild(js);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Write document to output.
     * 
     * @param services
     *            Services base.
     * @param document
     *            Output document.
     * @param output
     *            Output target file.
     */
    public void write(SRServices services, Document document, File output) {
        File parent = output.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalArgumentException("Could not create output directory '" + parent + "'.");
        }
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {
            fout = new FileOutputStream(output);
            bout = new BufferedOutputStream(fout);
            String encoding = UtilEncoding.getEncoding(services.lookup(IFeatureManager.class));
            new Serializer(bout, encoding).write(document);
        } catch (Exception e) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug(e.getMessage(), e);
            }
            throw new IllegalArgumentException(e);
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
            if (bout != null) {
                try {
                    bout.close();
                } catch (IOException e) {
                    if (UtilLog.LOG.isTraceEnabled()) {
                        UtilLog.LOG.trace(e.getMessage(), e);
                    }
                }
            }
        }
    }

}
