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
package org.specrunner.junit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.util.UtilLog;

/**
 * JUnit parametrized test helper. Example of use:
 * 
 * <pre>
 * public class TestParametrized extends SpecRunnerParametrized {
 * 
 *     public TestParametrized(Entry entry) {
 *         super(entry);
 *     }
 * 
 *     // JUnit annotation
 *     &#064;SuppressWarnings(&quot;unchecked&quot;)
 *     &#064;Parameters
 *     public static Collection&lt;Object[]&gt; getTestFiles() {
 *         File inDir = new File(&quot;src/test/resources/sources&quot;); // input directory
 *         File outDir = new File(&quot;src/test/resources/out&quot;); // output directory
 *         // files ending with 'e.html' OR starting with 'cust' OR containing 'st'
 *         return merge(endsWith(inDir, &quot;e.html&quot;, outDir), startsWith(inDir, &quot;cust&quot;, outDir), contains(inDir, &quot;st&quot;, outDir));
 *     }
 * }
 * </pre>
 * 
 * If you expect release all resources after each suite execution add a @AfterClass
 * to a static method as the following example.
 * 
 * <pre>
 * &#064;AfterClass
 * public static void releaseResources() {
 *     SpecRunnerServices.get().release();
 * }
 * </pre>
 * 
 * 
 * 
 * @author Thiago Santos
 * 
 */
@RunWith(Parameterized.class)
public class SpecRunnerParametrized {

    private Entry entry;

    protected SpecRunnerParametrized(Entry entry) {
        this.entry = entry;
    }

    protected static final class Entry {
        private Object input;
        private Object output;
        private IConfiguration cfg;

        private Entry(Object input, Object output, IConfiguration cfg) {
            this.input = input;
            this.output = output;
            this.cfg = cfg;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((cfg == null) ? 0 : cfg.hashCode());
            result = prime * result + ((input == null) ? 0 : input.hashCode());
            result = prime * result + ((output == null) ? 0 : output.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Entry other = (Entry) obj;
            return input.equals(other.input) && output.equals(other.output);
        }
    }

    @Test
    public void run() throws Exception {
        SpecRunnerJUnit.defaultRun(String.valueOf(entry.input), String.valueOf(entry.output), entry.cfg);
    }

    enum Filter {
        STARTWITH, CONTAINS, ENDSWITH;
    }

    protected static Collection<Object[]> startsWith(File inDir, String prefix, File outDir) {
        return startsWith(inDir, prefix, outDir, SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration());
    }

    protected static Collection<Object[]> startsWith(File inDir, String prefix, File outDir, IConfiguration cfg) {
        return filter(inDir, prefix, Filter.STARTWITH, outDir, cfg);
    }

    protected static Collection<Object[]> contains(File inDir, String suffix, File outDir) {
        return contains(inDir, suffix, outDir, SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration());
    }

    protected static Collection<Object[]> contains(File inDir, String suffix, File outDir, IConfiguration cfg) {
        return filter(inDir, suffix, Filter.CONTAINS, outDir, cfg);
    }

    protected static Collection<Object[]> endsWith(File inDir, String suffix, File outDir) {
        return endsWith(inDir, suffix, outDir, SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration());
    }

    protected static Collection<Object[]> endsWith(File inDir, String suffix, File outDir, IConfiguration cfg) {
        return filter(inDir, suffix, Filter.ENDSWITH, outDir, cfg);
    }

    protected static Collection<Object[]> filter(File inDir, final String str, final Filter filter, File outDir, IConfiguration cfg) {
        if (!inDir.exists()) {
            throw new IllegalArgumentException("Diretory '" + inDir + "' not found.");
        }
        if (!outDir.exists()) {
            if (!outDir.mkdirs()) {
                throw new IllegalArgumentException("Diretory '" + outDir + "' could not be created.");
            }
        }
        File[] accepted = inDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                switch (filter) {
                case STARTWITH:
                    return name.startsWith(str);
                case CONTAINS:
                    return name.contains(str);
                case ENDSWITH:
                    return name.endsWith(str);
                default:
                    return false;
                }
            }
        });
        List<Object[]> result = new LinkedList<Object[]>();
        for (File f : accepted) {
            result.add(new Object[] { new Entry(f, new File(outDir, f.getName()), cfg) });
            if (UtilLog.LOG.isInfoEnabled()) {
                UtilLog.LOG.info(filter + "(" + str + "): test '" + f + "' added.");
            }
        }
        return result;
    }

    protected static Collection<Object[]> merge(Collection<Object[]>... lists) {
        List<Object[]> result = new LinkedList<Object[]>();
        if (lists != null) {
            List<Entry> list = new LinkedList<Entry>();
            for (Collection<Object[]> l : lists) {
                for (Object[] o : l) {
                    Entry e = (Entry) o[0];
                    if (!list.contains(e)) {
                        list.add(e);
                        result.add(o);
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info(" MERGE: test '" + e.input + "' added.");
                        }
                    } else {
                        if (UtilLog.LOG.isInfoEnabled()) {
                            UtilLog.LOG.info("IGNORE: test '" + e.input + "' duplicated.");
                        }
                    }
                }
            }
        }
        return result;
    }
}