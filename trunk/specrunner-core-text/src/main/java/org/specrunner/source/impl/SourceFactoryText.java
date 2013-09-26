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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import nu.xom.Document;
import nu.xom.Element;

import org.specrunner.SpecRunnerServices;
import org.specrunner.source.SourceException;
import org.specrunner.util.xom.IPresenter;
import org.specrunner.util.xom.IPresenterManager;

public class SourceFactoryText extends AbstractSourceFactory {

    @Override
    protected Document fromTarget(URI uri, String target, String encoding) throws SourceException {
        Element root = new Element("html");
        Document doc = new Document(root);
        Element head = new Element("head");
        root.appendChild(head);
        Element body = new Element("body");
        root.appendChild(body);
        try {
            FileInputStream fin = new FileInputStream(new File(target));
            InputStreamReader isr = new InputStreamReader(fin, Charset.forName(encoding));
            BufferedReader bin = new BufferedReader(isr);
            Feature sc = new Feature("");
            // TODO: use language to set locale.
            String lang = bin.readLine().trim();
            if (lang != null) {
                readType(bin, sc, lang, Type.FEATURE);
                readFeatureDescription(bin, sc, bin.readLine());
            }
            IPresenter presenter = SpecRunnerServices.get(IPresenterManager.class).get(sc.getClass().getName());
            body.appendChild(presenter.asNode(sc, new Object[] { lang }));
            bin.close();
            isr.close();
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    private void readType(BufferedReader bin, Description d, String input, Type type) throws IOException {
        while (input != null && !(input = input.trim()).startsWith(type.text())) {
            input = bin.readLine();
        }
        if (input != null) {
            d.setName(input.substring(type.text().length()));
        }
    }

    private void readFeatureDescription(BufferedReader bin, Feature ft, String input) throws IOException {
        input = readNextGroup(bin, input, ft.getDescription());
        while (input != null) {
            if (input != null && (input = input.trim()).startsWith(Type.BACKGROUND.text())) {
                input = readNextGroup(bin, bin.readLine(), ft.getBackground());
            } else if (input != null && (input = input.trim()).startsWith(Type.FINALLY.text())) {
                input = readNextGroup(bin, bin.readLine(), ft.getFinallys());
            } else if (input != null && (input = input.trim()).startsWith(Type.SCENARIO.text())) {
                Scenario sc = new Scenario("");
                readType(bin, sc, input, Type.SCENARIO);
                input = readNextGroup(bin, bin.readLine(), sc.getDescription());
                ft.add(sc);
            } else if (input != null && (input = input.trim()).startsWith(Type.SCENARIO_OUTLINE.text())) {
                ScenarioOutline sc = new ScenarioOutline("");
                readType(bin, sc, input, Type.SCENARIO_OUTLINE);
                input = readNextGroup(bin, bin.readLine(), sc.getDescription());
                ft.add(sc);
                if (input != null && (input = input.trim()).startsWith(Type.EXAMPLES.text())) {
                    readExamples(bin, bin.readLine(), sc);
                }
            } else {
                input = bin.readLine();
            }
        }
    }

    private void readExamples(BufferedReader bin, String input, ScenarioOutline sc) throws IOException {
        if (input != null) {
            StringTokenizer st = new StringTokenizer(input.trim(), "|");
            while (st.hasMoreTokens()) {
                sc.add(st.nextToken());
            }
            input = bin.readLine();
            while (input != null && !(input = input.trim()).isEmpty()) {
                List<String> args = new LinkedList<String>();
                st = new StringTokenizer(input, "|");
                while (st.hasMoreTokens()) {
                    args.add(st.nextToken().trim());
                }
                sc.add(args);
                input = bin.readLine();
            }
        }
    }

    protected String readNextGroup(BufferedReader bin, String input, List<String> list) throws IOException {
        while (input != null && !(input = input.trim()).isEmpty()) {
            list.add(input.replace("<", "#{").replace(">", "}"));
            input = bin.readLine();
        }
        while (input != null && (input = input.trim()).isEmpty()) {
            input = bin.readLine();
        }
        return input;
    }
}
