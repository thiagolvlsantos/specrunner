/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2015  Thiago Santos

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
package org.specrunner.source.core;

import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.source.IBuilderFactory;
import org.specrunner.source.IEncoded;
import org.specrunner.util.UtilLog;

/**
 * Useful encoding services.
 * 
 * @author Thiago Santos
 * 
 */
public final class UtilEncoding {

    /**
     * Hidden constructor.
     */
    private UtilEncoding() {
        super();
    }

    /**
     * Get encoding information.
     * 
     * @return The set encoding.
     */
    public static String getEncoding() {
        return getEncoding(SRServices.getFeatureManager());
    }

    /**
     * Get encoding information.
     * 
     * @param fm
     *            Feature manager.
     * 
     * @return The set encoding.
     */
    public static String getEncoding(IFeatureManager fm) {
        String charset = null;
        try {
            charset = (String) fm.get(IBuilderFactory.FEATURE_ENCODING);
        } catch (Exception e) {
            if (UtilLog.LOG.isTraceEnabled()) {
                UtilLog.LOG.trace(e.getMessage(), e);
            }
        }
        if (charset == null) {
            charset = IEncoded.DEFAULT_ENCODING;
        }
        return charset;
    }
}
