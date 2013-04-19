package org.specrunner.sql.report;

import java.util.LinkedList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.sql.meta.Schema;
import org.specrunner.util.xom.IPresentation;

/**
 * A report two database comparation sharing the same schema definition. Only
 * differences as show.
 * 
 * @author Thiago Santos
 * 
 */
public class SchemaReport implements IPresentation {

    /**
     * The schema report.
     */
    private Schema schema;
    /**
     * List of table reports.
     */
    private List<TableReport> tables = new LinkedList<TableReport>();

    /**
     * Constructor.
     * 
     * @param schema
     *            The schema under analysis.
     */
    public SchemaReport(Schema schema) {
        this.schema = schema;
    }

    /**
     * Add a table report to the schema report.
     * 
     * @param tr
     *            A table report.
     */
    public void add(TableReport tr) {
        tables.add(tr);
    }

    /**
     * Indicates if report has useful information.
     * 
     * @return true, if report some differences.
     */
    public boolean isEmpty() {
        return tables.isEmpty();
    }

    @Override
    public String asString() {
        StringBuilder result = new StringBuilder();
        result.append(schema.getAlias() + "(" + schema.getName() + ")\n");
        for (TableReport r : tables) {
            result.append("\t" + r.asString() + "\n");
        }
        return result.toString();
    }

    @Override
    public Node asNode() {
        Element table = new Element("table");
        table.addAttribute(new Attribute("class", "sr_sreport"));
        Element caption = new Element("caption");
        table.appendChild(caption);
        {
            caption.addAttribute(new Attribute("title", schema.getName()));
            caption.addAttribute(new Attribute("class", "sr_sreport"));
            caption.appendChild(schema.getAlias());
        }
        for (TableReport r : tables) {
            Element tr = new Element("tr");
            table.appendChild(tr);
            {
                tr.addAttribute(new Attribute("class", "sr_sreport"));
                Element td = new Element("td");
                tr.appendChild(td);
                {
                    td.addAttribute(new Attribute("class", "sr_sreport"));
                    td.appendChild(r.asNode());
                }
            }
        }
        return table;
    }
}
