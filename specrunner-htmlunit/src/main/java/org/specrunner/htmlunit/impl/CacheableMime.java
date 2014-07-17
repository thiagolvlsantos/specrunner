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
package org.specrunner.htmlunit.impl;

import org.specrunner.htmlunit.ICacheable;
import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;

/**
 * Everything other that "text/html" mime type is cacheable.
 * 
 * @author Thiago Santos
 * 
 */
public class CacheableMime implements ICacheable {

    @Override
    public boolean isCacheable(WebRequest request, WebResponse response) {
        String mime = response.getContentType();
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("Mime type (" + mime + "): " + request.getUrl());
        }
        return !"".equals(mime) && !"text/html".equals(mime);
    }
}