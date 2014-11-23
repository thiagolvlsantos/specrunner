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
package org.specrunner.plugins.core.include;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Basic resolver implementation.
 * 
 * @author Thiago Santos.
 */
public class ResolverDefault implements IResolver {

    @Override
    public URI resolve(URI base, URI target) {
        URI resolve = resolveLocal(base, target);
        if (resolve == null) {
            resolve = base.resolve(target);
        }
        return resolve;
    }

    /**
     * Resolve as strings of files.
     * 
     * @param a
     *            Base.
     * @param b
     *            Relative.
     * @return A URI.
     */
    protected URI resolveLocal(URI a, URI b) {
        String strA = String.valueOf(a);
        String strB = String.valueOf(b);
        int pos = 0;
        while (pos < strA.length() && pos < strB.length()) {
            if (strA.charAt(pos) != strB.charAt(pos)) {
                break;
            }
            pos++;
        }
        if (pos > 0) {
            StringBuilder sb = new StringBuilder();
            int q = strA.indexOf('/', pos + 1);
            while (q > 0) {
                q = strA.indexOf('/', q + 1);
                sb.append("../");
            }
            sb.append(strB.substring(pos));
            try {
                return new URI(sb.toString());
            } catch (URISyntaxException e) {
                return null;
            }
        }
        return null;
    }
}