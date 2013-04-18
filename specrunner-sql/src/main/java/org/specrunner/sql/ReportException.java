package org.specrunner.sql;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SpecRunnerServices;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;
import org.specrunner.util.xom.IPresentation;

public class ReportException extends Exception implements IPresentation {

    public List<TableReport> tabl = new LinkedList<TableReport>();

    public static class TableReport implements IPresentation {
        public Table table;
        public List<LineReport> lines = new LinkedList<LineReport>();

        public TableReport(Table table) {
            this.table = table;
        }

        public static class LineReport implements IPresentation {
            enum Type {
                MISSING("-"), ALIEN("+"), DIFFERENT("!=");

                private String code;

                private Type(String code) {
                    this.code = code;
                }

                public String getStyle() {
                    return "sr_" + this.name().toLowerCase();
                }
            }

            public Type type;
            public Table table;
            public Map<String, Integer> indexes = new HashMap<String, Integer>();
            public List<Column> cols = new LinkedList<Column>();
            public List<Object> exp = new LinkedList<Object>();
            public List<Object> rec = new LinkedList<Object>();

            public LineReport(Type type, Table table) {
                this.type = type;
                this.table = table;
            }

            @Override
            public String asString() {
                StringBuilder sb = new StringBuilder();
                sb.append(type + "|");
                switch (type) {
                case ALIEN:
                    for (Column c : table.getColumns()) {
                        Integer index = indexes.get(c.getName());
                        if (index != null) {
                            sb.append(String.valueOf(rec.get(index)) + "|");
                        }
                    }
                    break;
                case MISSING:
                    for (Column c : table.getColumns()) {
                        Integer index = indexes.get(c.getName());
                        if (index != null) {
                            sb.append(String.valueOf(exp.get(index)) + "|");
                        }
                    }
                    break;
                case DIFFERENT:
                    for (int i = 0; i < cols.size(); i++) {
                        if (cols.get(i).isKey()) {
                            for (Column c : table.getColumns()) {
                                Integer index = indexes.get(c.getName());
                                if (index != null) {
                                    sb.append(String.valueOf(rec.get(index)) + "|");
                                } else {
                                    sb.append("|");
                                }
                            }
                        } else {
                            for (Column c : table.getColumns()) {
                                Integer index = indexes.get(c.getName());
                                if (index != null) {
                                    IStringAligner aligner = SpecRunnerServices.get(IStringAlignerFactory.class).align(String.valueOf(exp.get(i)), String.valueOf(rec.get(i)));
                                    DefaultAlignmentException def = new DefaultAlignmentException(aligner);
                                    sb.append(def.asString() + "|");
                                } else {
                                    sb.append("|");
                                }
                            }
                        }
                        break;
                    }
                }
                return sb.toString();
            }

            @Override
            public Node asNode() {
                Element tr = new Element("tr");
                tr.addAttribute(new Attribute("class", type.getStyle() + " sr_linereport"));
                Element td = new Element("td");
                td.addAttribute(new Attribute("class", type.getStyle()));
                tr.appendChild(td);
                td.appendChild(String.valueOf(type));
                switch (type) {
                case ALIEN:
                    line(tr, rec);
                    break;
                case MISSING:
                    line(tr, exp);
                    break;
                case DIFFERENT:
                    for (Column c : table.getColumns()) {
                        Integer index = indexes.get(c.getName());
                        if (index != null) {
                            td = new Element("td");
                            td.addAttribute(new Attribute("class", type.getStyle()));
                            tr.appendChild(td);
                            if (cols.get(index).isKey()) {
                                td.appendChild(String.valueOf(rec.get(index)));
                            } else {
                                td.addAttribute(new Attribute("class", td.getAttributeValue("class") + " " + type.getStyle() + "_cell"));
                                IStringAligner aligner = SpecRunnerServices.get(IStringAlignerFactory.class).align(String.valueOf(exp.get(index)), String.valueOf(rec.get(index)));
                                DefaultAlignmentException def = new DefaultAlignmentException(aligner);
                                td.appendChild(def.asNode());
                            }

                        } else {
                            tr.appendChild(new Element("td"));
                        }
                    }

                    break;
                }

                return tr;
            }

            protected void line(Element tr, List<Object> vals) {
                for (Column c : table.getColumns()) {
                    Integer index = indexes.get(c.getName());
                    if (index != null) {
                        Element td = new Element("td");
                        td.addAttribute(new Attribute("class", type.getStyle()));
                        tr.appendChild(td);
                        td.appendChild(String.valueOf(vals.get(index)));
                    } else {
                        tr.appendChild(new Element("td"));
                    }
                }
            }
        }

        @Override
        public String asString() {
            StringBuilder sb = new StringBuilder();
            sb.append(table.getAlias() + " (" + table.getName() + ")\n");
            int index = 0;
            for (LineReport lr : lines) {
                if (index++ == 0) {
                    sb.append("Detail");
                    for (Column c : table.getColumns()) {
                        sb.append(c.getAlias() + "(" + c.getName() + ")|");
                    }
                    sb.append("\n");
                }
                sb.append(lr.asString() + "\n");
            }
            return sb.toString();
        }

        @Override
        public Node asNode() {
            Element result = new Element("table");
            result.addAttribute(new Attribute("class", "sr_tablereport"));
            Element cap = new Element("caption");
            cap.addAttribute(new Attribute("title", table.getName()));
            cap.appendChild(table.getAlias());
            result.appendChild(cap);
            int index = 0;
            for (LineReport lr : lines) {
                if (index++ == 0) {
                    Element tr = new Element("tr");
                    result.appendChild(tr);
                    Element th = new Element("th");
                    th.addAttribute(new Attribute("class", "sr_linereport"));
                    th.appendChild("Detail");
                    tr.appendChild(th);
                    for (Column c : table.getColumns()) {
                        th = new Element("th");
                        th.addAttribute(new Attribute("class", "sr_linereport"));
                        th.appendChild(c.getAlias());
                        th.addAttribute(new Attribute("title", c.getName()));
                        tr.appendChild(th);
                    }
                }
                result.appendChild(lr.asNode());
            }
            return result;
        }
    }

    public boolean isEmpty() {
        return tabl.isEmpty();
    }

    @Override
    public String getMessage() {
        return asString();
    }

    @Override
    public String asString() {
        StringBuilder result = new StringBuilder();
        for (TableReport tr : tabl) {
            result.append(tr.asString() + "\n");
        }
        return result.toString();
    }

    @Override
    public Node asNode() {
        Element ul = new Element("ul");
        for (TableReport tr : tabl) {
            Element li = new Element("li");
            li.appendChild(tr.asNode());
            ul.appendChild(li);
        }
        return ul;
    }
}
