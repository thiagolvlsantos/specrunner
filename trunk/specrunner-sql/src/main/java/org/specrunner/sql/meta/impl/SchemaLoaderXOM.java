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
import java.util.LinkedList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Nodes;

import org.specrunner.SpecRunnerServices;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.ISchemaLoader;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.comparer.IComparator;
import org.specrunner.util.comparer.IComparatorManager;
import org.specrunner.util.converter.ConverterException;
import org.specrunner.util.converter.IConverter;
import org.specrunner.util.converter.IConverterManager;

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
            Element nSchema = d.getRootElement();
            s = new Schema().setName(nSchema.getAttributeValue("name")).setAlias(nSchema.getAttributeValue("alias"));
            Nodes nTables = nSchema.query("child::table");
            for (int i = 0; i < nTables.size(); i++) {
                Element nTable = (Element) nTables.get(i);
                Table t = new Table().setName(nTable.getAttributeValue("name")).setAlias(nTable.getAttributeValue("alias"));
                s.add(t);
                Nodes nColumns = nTable.query("child::column");
                for (int j = 0; j < nColumns.size(); j++) {
                    Element nColumn = (Element) nColumns.get(j);
                    Column c = new Column().setName(nColumn.getAttributeValue("name")).setAlias(nColumn.getAttributeValue("alias"));
                    t.add(c);
                    String key = nColumn.getAttributeValue("key");
                    c.setKey(key != null && Boolean.parseBoolean(key));
                    String converter = nColumn.getAttributeValue("converter");
                    if (converter != null) {
                        IConverterManager cm = SpecRunnerServices.get(IConverterManager.class);
                        IConverter instance = cm.get(converter);
                        if (instance == null) {
                            instance = (IConverter) Class.forName(converter).newInstance();
                            cm.bind(converter, instance);
                        }
                        c.setConverter(instance);
                    }
                    String comparator = nColumn.getAttributeValue("comparator");
                    if (comparator != null) {
                        IComparatorManager cm = SpecRunnerServices.get(IComparatorManager.class);
                        IComparator instance = cm.get(comparator);
                        if (instance == null) {
                            instance = (IComparator) Class.forName(comparator).newInstance();
                            cm.bind(comparator, instance);
                        }
                        c.setComparator(instance);
                    }
                    String defaultValue = nColumn.getAttributeValue("default");
                    IConverter conv = c.getConverter();
                    if (conv.accept(defaultValue)) {
                        List<String> args = new LinkedList<String>();
                        int index = 0;
                        String arg = nColumn.getAttributeValue("arg" + (index++));
                        while (arg != null) {
                            args.add(arg);
                            arg = nColumn.getAttributeValue("arg" + (index++));
                        }
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