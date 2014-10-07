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
package org.specrunner.dbms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.specrunner.dbms.BaseComparator;
import org.specrunner.dbms.ConfigurationFiles;

public class UtilIO {

    /**
     * Load object type from a file. Try first simple File reference, of not
     * exists, lookup in classpath.
     * 
     * @param type
     *            Class type of listed elements in file.
     * @param file
     *            File name to find.
     * @return A list of instances.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> load(Class<T> type, ConfigurationFiles files) {
        List<T> result = new LinkedList<T>();
        for (String file : files) {
            InputStream in = null;
            BufferedReader br = null;
            InputStreamReader fr = null;
            try {
                File f = new File(file);
                if (!f.exists()) {
                    in = BaseComparator.class.getResourceAsStream(file);
                    if (in == null) {
                        throw new RuntimeException("Invalid configuration file (" + type + "): " + file);
                    }
                } else {
                    in = new FileInputStream(f);
                }
                fr = new InputStreamReader(in);
                br = new BufferedReader(fr);
                String input;
                while ((input = br.readLine()) != null) {
                    result.add((T) Class.forName(input.trim()).newInstance());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return result;
    }
}