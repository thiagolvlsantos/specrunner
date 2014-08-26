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
package org.specrunner.sql.meta.impl;

import java.io.IOException;
import java.io.InputStream;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Nodes;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.ISchemaLoaderXML;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.INodeHolder;
import org.specrunner.util.xom.INodeHolderFactory;

/**
 * A loader to Schema from XML files.
 * 
 * @author Thiago Santos
 * 
 */
public class SchemaLoaderXOM implements ISchemaLoaderXML {
    /**
     * XML parser.
     */
    protected Builder builder;

    @Override
    public Schema load(IContext context, Object source) {
        Schema schema = null;
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(String.valueOf(source));
            if (in == null) {
                throw new RuntimeException("Resource '" + source + "' not found.");
            }
            Document d = getBuilder().build(in);

            INodeHolderFactory holderFactory = SRServices.get(INodeHolderFactory.class);
            INodeHolder nSchema = holderFactory.create(d.getRootElement());
            schema = new Schema();
            schema.setName(nSchema.getAttribute(ATTR_NAME)).setAlias(nSchema.getAttribute(ATTR_ALIAS, schema.getName()));

            Nodes nTables = nSchema.getNode().query("child::table");
            for (int i = 0; i < nTables.size(); i++) {
                INodeHolder nTable = holderFactory.create(nTables.get(i));
                Table table = new Table();
                table.setName(nTable.getAttribute(ATTR_NAME)).setAlias(nTable.getAttribute(ATTR_ALIAS, table.getName()));
                schema.add(table);

                Nodes nColumns = nTable.getNode().query("child::column");
                for (int j = 0; j < nColumns.size(); j++) {
                    INodeHolder nColumn = holderFactory.create(nColumns.get(j));
                    Column column = new Column();
                    UtilSchema.setupColumn(context, table, column, nColumn);
                    table.add(column);
                }
            }
        } catch (Exception e) {
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info(e.getMessage(), e);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                if (UtilLog.LOG.isDebugEnabled()) {
                    UtilLog.LOG.debug(e.getMessage(), e);
                }
                throw new RuntimeException(e);
            }
        }
        return schema;
    }

    /**
     * Create a builder to read schema information.
     * 
     * @return A builder.
     */
    protected Builder getBuilder() {
        if (builder == null) {
            builder = new Builder();
        }
        return builder;
    }
}