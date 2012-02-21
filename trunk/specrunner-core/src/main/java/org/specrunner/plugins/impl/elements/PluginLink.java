/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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
package org.specrunner.plugins.impl.elements;

import java.net.URI;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.source.ISource;
import org.specrunner.source.ISourceFactory;
import org.specrunner.source.SourceException;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.source.resource.positional.Position;
import org.specrunner.util.UtilLog;

/**
 * Add resources of 'link' tags.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginLink extends AbstractPluginResources {

    private String type;
    private String href;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        if (type != null && href != null && isSave()) {
            try {
                ISource s = context.getSources().peek();
                ISourceFactory sf = s.getFactory();
                ISource o = sf.newSource(href);
                URI path = s.resolve(o).getURI();
                if (isCss()) {
                    s.getManager().addCss(String.valueOf(path), false, EType.BINARY, Position.HEAD_START);
                    if (UtilLog.LOG.isInfoEnabled()) {
                        UtilLog.LOG.info("CSS " + href + " from source " + s.getString() + " added.");
                    }
                }
            } catch (SourceException e) {
                throw new PluginException(e);
            } catch (ResourceException e) {
                throw new PluginException(e);
            }
        }
        return ENext.DEEP;
    }

    protected boolean isCss() {
        return "text/css".equalsIgnoreCase(type);
    }
}