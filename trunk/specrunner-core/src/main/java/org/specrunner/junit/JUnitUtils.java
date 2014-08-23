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
package org.specrunner.junit;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.specrunner.SRServices;
import org.specrunner.listeners.IScenarioListener;
import org.specrunner.source.ISourceFactoryManager;
import org.specrunner.util.UtilLog;

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
        String str;
        try {
            str = new File(location.toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            throw new RuntimeException("Test class must be in a package.");
        }
        // exact match
        String path = str + File.separator + pkg.getName().replace(".", File.separator) + File.separator;
        String exact = path + clazz.getSimpleName();
        Set<String> extensions = SRServices.get(ISourceFactoryManager.class).keySet();
        for (String s : extensions) {
            File tmp = new File(exact + "." + s);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Looking for " + tmp);
            }
            if (tmp.exists()) {
                return tmp;
            }
        }
        // remove 'Test' part.
        String clean = path + clazz.getSimpleName().replace("Test", "");
        for (String s : extensions) {
            File tmp = new File(clean + "." + s);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Looking for " + tmp);
            }
            if (tmp.exists()) {
                return tmp;
            }
        }
        throw new RuntimeException("File with one of extensions '" + extensions + "' to " + exact + " not found!");
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

    /**
     * Given a class return instances for scenario listeners.
     * 
     * @param type
     *            The base object type.
     * @return An array of listener instances.
     */
    public static IScenarioListener[] getScenarioListener(Class<?> type) {
        List<Class<? extends IScenarioListener>> scan = scanAnnotation(type);
        if (scan.isEmpty()) {
            return new IScenarioListener[0];
        }
        IScenarioListener[] result = new IScenarioListener[scan.size()];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = scan.get(i).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Get full list of scenario listeners in annotations.
     * 
     * @param type
     *            A type.
     * @return A list of scenario listeners classes for the given type, in
     *         order, from top most class to bottom class.
     */
    public static List<Class<? extends IScenarioListener>> scanAnnotation(Class<?> type) {
        List<Class<? extends IScenarioListener>> scan = new LinkedList<Class<? extends IScenarioListener>>();
        Class<?> tmp = type;
        while (tmp != Object.class) {
            SRScenarioListeners local = tmp.getAnnotation(SRScenarioListeners.class);
            if (local != null) {
                Class<? extends IScenarioListener>[] value = local.value();
                if (value != null) {
                    for (int i = 0; i < value.length; i++) {
                        scan.add(0, value[i]);
                    }
                }
                if (!local.inheritListeners()) {
                    break;
                }
            }
            tmp = tmp.getSuperclass();
        }
        return scan;
    }
}