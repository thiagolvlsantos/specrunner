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
package org.specrunner.htmlunit.impl;

import java.io.IOException;

import org.specrunner.util.UtilLog;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.WebConnectionWrapper;

/**
 * A logging connection.
 * 
 * @author Thiago Santos
 * 
 */
public class WebConnectionLogging extends WebConnectionWrapper {

    /**
     * Creates a logging from a client.
     * 
     * @param webClient
     *            A client.
     */
    public WebConnectionLogging(WebClient webClient) {
        super(webClient);
    }

    /**
     * Creates a logging from a connection.
     * 
     * @param webConnection
     *            A web connection.
     */
    public WebConnectionLogging(WebConnection webConnection) {
        super(webConnection);
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {
        WebResponse result = super.getResponse(request);
        if (UtilLog.LOG.isInfoEnabled()) {
            UtilLog.LOG.info("New response '" + request.getUrl() + "'->" + result + ".");
        }
        return result;
    }
}