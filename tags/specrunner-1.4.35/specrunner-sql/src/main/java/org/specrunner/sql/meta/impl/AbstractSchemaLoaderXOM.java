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

import nu.xom.Document;
import nu.xom.Nodes;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.ISchemaLoaderXML;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.xom.node.INodeHolder;
import org.specrunner.util.xom.node.INodeHolderFactory;

/**
 * A loader of Schema from a Document.
 * 
 * @author Thiago Santos
 */
public abstract class AbstractSchemaLoaderXOM implements ISchemaLoaderXML {

    /**
     * Load a document from a context and a document.
     * 
     * @param context
     *            A context.
     * @param document
     *            A document.
     * @return A schema loader.
     * @throws Exception
     *             On loading errors.
     */
    protected Schema loadDocument(IContext context, Document document) throws Exception {
        Schema schema = new Schema();
        INodeHolderFactory holderFactory = SRServices.get(INodeHolderFactory.class);
        INodeHolder nSchema = holderFactory.newHolder(document.getRootElement());
        schema.setName(nSchema.getAttribute(ATTR_NAME)).setAlias(nSchema.getAttribute(ATTR_ALIAS, schema.getName()));

        Nodes nTables = nSchema.getNode().query("child::table");
        for (int i = 0; i < nTables.size(); i++) {
            INodeHolder nTable = holderFactory.newHolder(nTables.get(i));
            Table table = new Table();
            table.setName(nTable.getAttribute(ATTR_NAME)).setAlias(nTable.getAttribute(ATTR_ALIAS, table.getName()));
            schema.add(table);

            Nodes nColumns = nTable.getNode().query("child::column");
            for (int j = 0; j < nColumns.size(); j++) {
                INodeHolder nColumn = holderFactory.newHolder(nColumns.get(j));
                Column column = new Column();
                UtilSchema.setupColumn(context, table, column, nColumn);
                table.add(column);
            }
        }
        return schema;
    }
}