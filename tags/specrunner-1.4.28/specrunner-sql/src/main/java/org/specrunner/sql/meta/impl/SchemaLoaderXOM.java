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

import org.specrunner.context.IContext;
import org.specrunner.sql.meta.Schema;
import org.specrunner.util.UtilLog;

/**
 * A loader of Schema from XML files.
 * 
 * @author Thiago Santos
 * 
 */
public class SchemaLoaderXOM extends AbstractSchemaLoaderXOM {
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
            schema = loadDocument(context, getBuilder().build(in));
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