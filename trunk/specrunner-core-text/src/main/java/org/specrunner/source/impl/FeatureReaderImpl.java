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
package org.specrunner.source.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.specrunner.SpecRunnerServices;
import org.specrunner.util.cache.ICache;
import org.specrunner.util.cache.ICacheFactory;

/**
 * Basic implementation of reature reader.
 * 
 * @author Thiago Santos
 * 
 */
public class FeatureReaderImpl implements IFeatureReader {

    /**
     * Small size, only for languages.
     */
    private static final int TEN = 10;
    /**
     * The keyword set.
     */
    private static ICache<String, Keywords> cacheKeywords = SpecRunnerServices.get(ICacheFactory.class).newCache(SourceFactoryText.class.getName());

    static {
        cacheKeywords.setSize(TEN);
    }

    @Override
    public Feature load(InputStream in, String encoding) throws IOException {
        InputStreamReader isr = null;
        BufferedReader bin = null;
        try {
            isr = new InputStreamReader(in, Charset.forName(encoding));
            bin = new BufferedReader(isr);

            String lang = bin.readLine();
            if (lang != null) {
                lang = lang.substring(lang.lastIndexOf(':') + 1, lang.length()).trim();
            } else {
                lang = "en";
            }
            Keywords keywords = cacheKeywords.get(lang);
            if (keywords == null) {
                keywords = new Keywords(ResourceBundle.getBundle("sr_gherkin", new Locale(lang)));
                cacheKeywords.put(lang, keywords);
            }
            Feature feature = new Feature("");
            feature.setKeywords(keywords);
            if (lang != null) {
                readKeyword(bin, lang, keywords.getFeature(), feature);
                readFeatureDescription(bin, bin.readLine(), keywords, feature);
            }
            return feature;
        } catch (IOException e) {
            throw e;
        } finally {
            if (bin != null) {
                try {
                    bin.close();
                } catch (IOException e) {
                    throw e;
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }

    /**
     * Read a keyword.
     * 
     * @param reader
     *            The input.
     * @param line
     *            Current line.
     * @param keyword
     *            The word.
     * @param named
     *            The description object.
     * @throws IOException
     *             On reading error.
     */
    protected void readKeyword(BufferedReader reader, String line, String keyword, NamedSentence named) throws IOException {
        while (line != null) {
            line = line.trim();
            if (line.startsWith(keyword)) {
                break;
            }
            line = reader.readLine();
        }
        if (line != null) {
            named.setName(line.substring(keyword.length()));
        }
    }

    /**
     * Read feature description.
     * 
     * @param reader
     *            The reader.
     * @param line
     *            The line.
     * @param words
     *            The word map.
     * @param feature
     *            The feature.
     * @throws IOException
     *             On reading error.
     */
    protected void readFeatureDescription(BufferedReader reader, String line, Keywords words, Feature feature) throws IOException {
        line = readNextGroup(reader, line, feature.getDescription());
        while (line != null) {
            line = line.trim();
            if (line.startsWith(words.getBackground())) {
                line = readNextGroup(reader, reader.readLine(), feature.getBackground());
            } else if (line.startsWith(words.getFinally())) {
                line = readNextGroup(reader, reader.readLine(), feature.getFinallys());
            } else if (line.startsWith(words.getScenario())) {
                Scenario sc = new Scenario("");
                readKeyword(reader, line, words.getScenario(), sc);
                line = readNextGroup(reader, reader.readLine(), sc.getDescription());
                feature.add(sc);
            } else if (line.startsWith(words.getScenarioOutline())) {
                ScenarioOutline sc = new ScenarioOutline("");
                readKeyword(reader, line, words.getScenarioOutline(), sc);
                line = readNextGroup(reader, reader.readLine(), sc.getDescription());
                feature.add(sc);
                if (line != null) {
                    line = line.trim();
                    if (line.startsWith(words.getExamples())) {
                        readExamples(reader, reader.readLine(), sc);
                    }
                }
            } else {
                line = reader.readLine();
            }
        }
    }

    /**
     * Read next group.
     * 
     * @param reader
     *            The reader.
     * @param line
     *            The current line.
     * @param group
     *            The group to add lines.
     * @return The next line right after ending.
     * @throws IOException
     *             On reading errors.
     */
    protected String readNextGroup(BufferedReader reader, String line, List<String> group) throws IOException {
        while (line != null) {
            line = line.trim();
            if (line.isEmpty()) {
                break;
            }
            group.add(line);
            line = reader.readLine();
        }
        while (line != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                break;
            }
            line = reader.readLine();
        }
        return line;
    }

    /**
     * Read examples.
     * 
     * @param reader
     *            The reader.
     * @param line
     *            The input line.
     * @param outline
     *            The parent scenario.
     * @throws IOException
     *             On reading errors.
     */
    protected void readExamples(BufferedReader reader, String line, ScenarioOutline outline) throws IOException {
        if (line != null) {
            StringTokenizer st = new StringTokenizer(line.trim(), "|");
            while (st.hasMoreTokens()) {
                outline.add(st.nextToken());
            }
            line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    break;
                }
                List<String> args = new LinkedList<String>();
                st = new StringTokenizer(line, "|");
                while (st.hasMoreTokens()) {
                    args.add(st.nextToken().trim());
                }
                outline.add(args);
                line = reader.readLine();
            }
        }
    }
}