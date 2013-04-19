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
                sb.append("\tDetail");
                for (Column c : table.getColumns()) {
                    sb.append(c.getAlias() + "(" + c.getName() + ")|");
                }
                sb.append("\n");
            }
            sb.append("\t" + lr.asString() + "\n");
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
                cap.addAttribute(new Attribute("title", table.getName()));
                cap.addAttribute(new Attribute("class", "sr_treport"));
                cap.appendChild(table.getAlias());
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
                    for (Column c : table.getColumns()) {
                        th = new Element("th");
                        tr.appendChild(th);
                        {
                            th.addAttribute(new Attribute("title", c.getName()));
                            th.addAttribute(new Attribute("class", "sr_lreport"));
                            th.appendChild(c.getAlias());
                        }
                    }
                }
                result.appendChild(lr.asNode());
            }
        }
        return result;
    }
}
