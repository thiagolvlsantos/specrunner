/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package org.specrunner.junit;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.specrunner.SRServices;
import org.specrunner.source.ISourceFactoryManager;

/**
 * JUnit useful functions.
 * 
 * @author Thiago Santos
 * 
 */
public final class JUnitUtils {

    /**
     * Output path.
     */
    public static final String PATH = System.getProperty("sr.output", "target/output/");

    /**
     * Default constructor.
     */
    private JUnitUtils() {
    }

    /**
     * Get the HTML file corresponding to the Java.
     * 
     * @param clazz
     *            The test class.
     * 
     * @return The input HTML file.
     */
    public static File getFile(Class<?> clazz) {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        String str = location.toString();
        str = str.replace("file:\\\\", "").replace("file:///", "").replace("file:\\", "").replace("file:/", "");
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            throw new RuntimeException("Test classe must be in a package.");
        }
        // exact match
        String prefix = str + pkg.getName().replace(".", File.separator) + File.separator + clazz.getSimpleName();
        Set<String> extensions = SRServices.get(ISourceFactoryManager.class).keySet();
        for (String s : extensions) {
            File tmp = new File(prefix + "." + s);
            if (tmp.exists()) {
                return tmp;
            }
        }
        // remove 'Test' part.
        prefix = str + pkg.getName().replace(".", File.separator) + File.separator + clazz.getSimpleName().replace("Test", "");
        for (String s : extensions) {
            File tmp = new File(prefix + "." + s);
            if (tmp.exists()) {
                return tmp;
            }
        }
        throw new RuntimeException("File with one of extensions '" + extensions + "' to " + prefix + " not found!");
    }

    /**
     * Get the output file.
     * 
     * @param clazz
     *            The test class.
     * @param input
     *            The input file.
     * @return The output file.
     */
    public static File getOutput(Class<?> clazz, File input) {
        return new File(new File(PATH + clazz.getPackage().getName().replace('.', File.separatorChar)).getAbsoluteFile(), input.getName());
    }

    /**
     * Get the output name adjusted.
     * 
     * @param name
     *            The original name.
     * @return The adjusted name. ie. Excel (.xls,.xlsx) test files are
     *         transformed to HTML (.html).
     */
    public static String getOutputName(String name) {
        if (name != null && name.contains(".") && !name.endsWith(".html")) {
            return name.substring(0, name.lastIndexOf('.')) + ".html";
        } else {
            return name;
        }
    }
}