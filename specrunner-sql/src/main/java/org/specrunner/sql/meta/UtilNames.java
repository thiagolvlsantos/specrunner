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
package org.specrunner.sql.meta;

import java.util.StringTokenizer;

import org.specrunner.SRServices;
import org.specrunner.util.string.IStringNormalizer;

/**
 * Utility names.
 * 
 * @author Thiago Santos
 */
public final class UtilNames {

    /**
     * Default constructor.
     */
    private UtilNames() {
    }

    /**
     * Normalize alias.
     * 
     * @param alias
     *            A alias.
     * @return Normalized alias.
     */
    public static String normalize(String alias) {
        if (alias == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(alias);
        IStringNormalizer normalizer = SRServices.get(IStringNormalizer.class);
        for (int i = 0; st.hasMoreTokens(); i++) {
            sb.append((i == 0 ? "" : " ") + normalizer.clean(st.nextToken().toLowerCase()));
        }
        return sb.toString();
    }
}
