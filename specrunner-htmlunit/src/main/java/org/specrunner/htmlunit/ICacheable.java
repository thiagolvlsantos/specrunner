/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package org.specrunner.htmlunit;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;

/**
 * Answer if a request and(or) response can be cached.
 * 
 * @author Thiago Santos
 * 
 */
public interface ICacheable {

    /**
     * Check if a request/response is cacheable.
     * 
     * @param request
     *            The request.
     * @param response
     *            The response.
     * @return true, if cacheable, false, otherwise.
     */
    boolean isCacheable(WebRequest request, WebResponse response);

}
