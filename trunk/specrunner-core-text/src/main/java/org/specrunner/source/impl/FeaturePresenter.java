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

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.SpecRunnerServices;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.data.PluginMap;
import org.specrunner.plugins.impl.flow.PluginIterator;
import org.specrunner.plugins.impl.language.PluginSentence;
import org.specrunner.plugins.impl.macro.PluginCall;
import org.specrunner.plugins.impl.macro.PluginMacro;
import org.specrunner.util.UtilString;
import org.specrunner.util.xom.IPresenter;

/**
 * Perform conversion of an object <code>Feature</code> to a <code>Node</code>.
 * 
 * @author Thiago Santos
 * 
 */
@SuppressWarnings("serial")
public class FeaturePresenter implements IPresenter {

    @Override
    public void initialize() {
    }

    @Override
    public Node asNode(Object obj, Object... args) {
        if (!(obj instanceof Feature)) {
            throw new IllegalArgumentException("Invalid source instance:" + obj + " of type " + (obj != null ? obj.getClass() : null));
        }
        Feature feature = (Feature) obj;
        Keywords keywords = feature.getKeywords();
        if (keywords == null) {
            throw new IllegalArgumentException("Feature keywords not set.");
        }
        return dumpFeature(new Element("div"), keywords, feature);
    }

    /**
     * Dump feature to root node.
     * 
     * @param root
     *            The node.
     * @param words
     *            The keyword mapping.
     * @param feature
     *            The feature.
     * @return The node.
     */
    protected Node dumpFeature(Element root, Keywords words, Feature feature) {
        dumpDescription(root, words.getFeature(), feature, "h1");
        dumpList(root, feature.getSentences());
        List<Scenario> scenarios = feature.getScenarios();
        if (!scenarios.isEmpty()) {
            for (Scenario s : scenarios) {
                dumpScenario(root, words, s);
            }
        }
        return root;
    }

    /**
     * Dump description.
     * 
     * @param root
     *            The node.
     * @param keyword
     *            The keyword.
     * @param description
     *            The content object.
     * @param tag
     *            The tag to be added.
     */
    protected void dumpDescription(Element root, String keyword, NamedSentences description, String tag) {
        Element e = new Element(tag);
        root.appendChild(e);
        e.appendChild(keyword + (description != null ? description.getName() : ""));
    }

    /**
     * Dump a list.
     * 
     * @param root
     *            The node.
     * @param list
     *            The list to dump.
     */
    protected void dumpList(Element root, List<Sentence> list) {
        if (list != null && !list.isEmpty()) {
            Element quote = new Element("blockquote");
            root.appendChild(quote);
            for (Sentence s : list) {
                quote.appendChild(s.getText());
                quote.appendChild(new Element("br"));
            }
        }
    }

    /**
     * Dump a scenario.
     * 
     * @param root
     *            The node.
     * @param words
     *            The keyword mapping.
     * @param scenario
     *            The scenario.
     */
    protected void dumpScenario(Element root, Keywords words, Scenario scenario) {
        Element divScenario = new Element("div");
        root.appendChild(divScenario);
        if (scenario instanceof ScenarioOutline) {
            dumpMultiScenario(divScenario, words, (ScenarioOutline) scenario);
        } else {
            dumpSingleScenario(divScenario, words, scenario);
        }
    }

    /**
     * Dump scenario outlines.
     * 
     * @param root
     *            The node.
     * @param words
     *            The keyword mapping.
     * @param scenario
     *            The scenario outline.
     */
    protected void dumpMultiScenario(Element root, Keywords words, ScenarioOutline scenario) {
        dumpExamples(root, words, scenario, dumpMacro(root, words, scenario));
    }

    /**
     * Dump the scenario as a macro from SpecRunner core.
     * 
     * @param root
     *            The root.
     * @param words
     *            The words.
     * @param scenario
     *            The scenario.
     * @return The newly created macro name.
     */
    protected String dumpMacro(Element root, Keywords words, Scenario scenario) {
        Element macro = new Element("div");
        root.appendChild(macro);
        String alias;
        try {
            alias = SpecRunnerServices.get(IPluginFactory.class).getAlias(PluginMacro.class);
        } catch (PluginException e) {
            alias = "macro";
        }
        macro.addAttribute(new Attribute("class", alias));
        String name = UtilString.camelCase(scenario.getName());
        macro.addAttribute(new Attribute("name", name));
        {
            dumpDescription(macro, words.getScenarioOutline(), scenario, "h2");
            dumpExecutableList(macro, scenario.getParent().getBackground(), true);
            dumpExecutableList(macro, scenario.getSentences(), true);
            dumpExecutableList(macro, scenario.getParent().getFinallys(), true);
        }
        return name;
    }

    /**
     * Dump an executable list.
     * 
     * @param root
     *            The node.
     * @param list
     *            The executable list.
     * @param replace
     *            Flga to replace &gt; and &lt; by their corresponding in SR.
     */
    protected void dumpExecutableList(Element root, List<Sentence> list, boolean replace) {
        Element quote = new Element("blockquote");
        String alias;
        try {
            alias = SpecRunnerServices.get(IPluginFactory.class).getAlias(PluginSentence.class);
        } catch (PluginException e) {
            alias = "sentence";
        }
        for (Sentence s : list) {
            Element sentence = new Element("span");
            sentence.appendChild(replace ? normalize(s.getText()) : s.getText());
            sentence.addAttribute(new Attribute("class", alias));
            quote.appendChild(sentence);
            quote.appendChild(new Element("br"));
            if (s.hasData()) {
                Element arg = new Element("arg");
                arg.addAttribute(new Attribute("value", "$XML"));
                dumpTable(arg, s.getData());
                sentence.appendChild(arg);
            }
            if (s.hasMessage()) {
                Element message = new Element("arg");
                message.appendChild(replace ? normalize(s.getMessage()) : s.getMessage());
                sentence.appendChild(message);
            }
        }
        if (quote.getChildCount() > 0) {
            root.appendChild(quote);
        }
    }

    /**
     * Prepare placeholders.
     * 
     * @param text
     *            The text to adjust.
     * @return The placeholder adjusted.
     */
    protected String normalize(String text) {
        return text.replace("<", "#{").replace(">", "}");
    }

    /**
     * Dump the scenario examples.
     * 
     * @param root
     *            The node.
     * @param words
     *            The keyword mapping.
     * @param outline
     *            The scenario outline.
     * @param name
     *            The macro name.
     */
    protected void dumpExamples(Element root, Keywords words, ScenarioOutline outline, String name) {
        Element examples = new Element("div");
        root.appendChild(examples);
        {
            dumpDescription(examples, words.getExamples(), null, "h3");
            dumpMap(examples, outline);
            dumpIterator(examples, name);
        }
    }

    /**
     * Dump example table as a mapping with attributes named.
     * 
     * @param root
     *            The root.
     * @param outline
     *            The scenario outline.
     */
    protected void dumpMap(Element root, ScenarioOutline outline) {
        Element quote = new Element("blockquote");
        root.appendChild(quote);
        {
            dumpTable(quote, outline.getTable());
        }
    }

    /**
     * Dump table information.
     * 
     * @param root
     *            The root node.
     * @param data
     *            The table.
     */
    protected void dumpTable(Element root, DataTable data) {
        Element table = new Element("table");
        String alias;
        try {
            alias = SpecRunnerServices.get(IPluginFactory.class).getAlias(PluginMap.class);
        } catch (PluginException e1) {
            alias = "map";
        }
        table.addAttribute(new Attribute("class", alias));
        table.addAttribute(new Attribute("name", "examples"));
        table.addAttribute(new Attribute("scope", "div"));
        root.appendChild(table);
        {
            Element tr = new Element("tr");
            table.appendChild(tr);
            for (String str : data.getNames()) {
                Element td = new Element("td");
                td.appendChild(str);
                tr.appendChild(td);
            }
            for (List<String> e : data.getExamples()) {
                tr = new Element("tr");
                for (String str : e) {
                    Element td = new Element("td");
                    td.appendChild(str);
                    tr.appendChild(td);
                }
                table.appendChild(tr);
            }
        }
    }

    /**
     * Dump the iterator to call examples.
     * 
     * @param root
     *            The node.
     * @param name
     *            The macro name.
     */
    protected void dumpIterator(Element root, String name) {
        Element iterator = new Element("span");
        String alias;
        try {
            alias = SpecRunnerServices.get(IPluginFactory.class).getAlias(PluginIterator.class);
        } catch (PluginException e) {
            alias = "iterator";
        }
        iterator.addAttribute(new Attribute("class", alias));
        iterator.addAttribute(new Attribute("name", "examples"));
        root.appendChild(iterator);
        {
            dumpCall(iterator, name);
        }
    }

    /**
     * Dump the macro call.
     * 
     * @param root
     *            The root.
     * @param name
     *            The macro name.
     */
    protected void dumpCall(Element root, String name) {
        Element h4 = new Element("h4");
        h4.appendChild("Example ${index+1}:");
        root.appendChild(h4);
        {
            Element quo = new Element("blockquote");
            root.appendChild(quo);
            {
                Element call = new Element("p");
                String alias;
                try {
                    alias = SpecRunnerServices.get(IPluginFactory.class).getAlias(PluginCall.class);
                } catch (PluginException e) {
                    alias = "call";
                }
                call.addAttribute(new Attribute("class", alias));
                call.addAttribute(new Attribute("name", name));
                quo.appendChild(call);
            }
        }
    }

    /**
     * Dump a scenario without examples.
     * 
     * @param root
     *            The node.
     * @param words
     *            The keyword mapping.
     * @param scenario
     *            The scenario.
     */
    public void dumpSingleScenario(Element root, Keywords words, Scenario scenario) {
        dumpDescription(root, words.getScenario(), scenario, "h2");
        dumpExecutableList(root, scenario.getParent().getBackground(), false);
        dumpExecutableList(root, scenario.getSentences(), false);
        dumpExecutableList(root, scenario.getParent().getFinallys(), false);
    }

    @Override
    public String asString(Object obj, Object... args) {
        if (!(obj instanceof Feature)) {
            return null;
        }
        return null;
    }
}