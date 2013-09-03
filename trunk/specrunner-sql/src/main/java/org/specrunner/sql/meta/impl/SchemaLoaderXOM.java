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
package org.specrunner.sql.meta.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;

import org.specrunner.converters.ConverterException;
import org.specrunner.converters.IConverter;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.ISchemaLoader;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.xom.INodeHolder;
import org.specrunner.util.xom.UtilNode;

/**
 * A loader to Schema from XML files.
 * 
 * @author Thiago Santos
 * 
 */
public class SchemaLoaderXOM implements ISchemaLoader {
    /**
     * XML parser.
     */
    private Builder builder = new Builder();

    @Override
    public Schema load(Object source) {
        Schema s = null;
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(String.valueOf(source));
            if (in == null) {
                throw new RuntimeException("Resource '" + source + "' not found.");
            }
            Document d = builder.build(in);
            INodeHolder nSchema = UtilNode.newNodeHolder(d.getRootElement());
            s = new Schema().setName(nSchema.getAttribute("name")).setAlias(nSchema.getAttribute("alias"));
            Nodes nTables = nSchema.getNode().query("child::table");
            for (int i = 0; i < nTables.size(); i++) {
                INodeHolder nTable = UtilNode.newNodeHolder(nTables.get(i));
                Table t = new Table().setName(nTable.getAttribute("name")).setAlias(nTable.getAttribute("alias"));
                s.add(t);
                Nodes nColumns = nTable.getNode().query("child::column");
                for (int j = 0; j < nColumns.size(); j++) {
                    INodeHolder nColumn = UtilNode.newNodeHolder(nColumns.get(j));
                    Column c = new Column().setName(nColumn.getAttribute("name")).setAlias(nColumn.getAttribute("alias"));
                    t.add(c);
                    String key = nColumn.getAttribute("key");
                    c.setKey(key != null && Boolean.parseBoolean(key));
                    if (nColumn.hasAttribute("converter")) {
                        c.setConverter(nColumn.getConverter());
                    }
                    if (nColumn.hasAttribute("comparator")) {
                        c.setComparator(nColumn.getComparator());
                    }
                    String defaultValue = nColumn.getAttribute("default");
                    IConverter conv = c.getConverter();
                    if (conv.accept(defaultValue)) {
                        List<String> args = nColumn.getArguments();
                        Object obj;
                        try {
                            obj = conv.convert(defaultValue, args.isEmpty() ? null : args.toArray());
                        } catch (ConverterException e) {
                            throw new RuntimeException("Convertion error at table: " + t.getName() + ", column: " + c.getName() + ". Attempt to convert default value '" + defaultValue + "' using a '" + conv + "'.", e);
                        }
                        c.setDefaultValue(obj);
                    } else {
                        c.setDefaultValue(conv.convert(defaultValue, null));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return s;
    }
}