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
            caption.addAttribute(new Attribute("class", "sr_sreport"));
            caption.appendChild(schema.getAlias());

            Element sup = new Element("sup");
            sup.addAttribute(new Attribute("class", "sr_treport"));
            sup.appendChild(schema.getName());
            caption.appendChild(sup);
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
                td.appendChild(new Element("p"));
            }
        }
        return table;
    }
}
