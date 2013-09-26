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

public class PresenterFeature implements IPresenter {

    @Override
    public void initialize() {
    }

    @Override
    public Node asNode(Object obj, Object[] args) {
        if (!(obj instanceof Feature)) {
            return null;
        }
        return addFeature(new Element("div"), (Feature) obj);
    }

    protected Node addFeature(Element root, Feature feature) {
        addDescription(root, Type.FEATURE, feature, "h1");

        list(root, feature.getDescription());

        List<Scenario> scenarios = feature.getScenarios();
        if (!scenarios.isEmpty()) {
            for (Scenario s : scenarios) {
                addScenario(root, s);
            }
        }
        return root;
    }

    protected void addDescription(Element root, Type type, Description description, String tag) {
        Element e = new Element(tag);
        root.appendChild(e);

        e.appendChild(type.text() + (description != null ? description.getName() : ""));
    }

    protected void list(Element root, List<String> list) {
        if (list != null && !list.isEmpty()) {
            Element quote = new Element("blockquote");
            root.appendChild(quote);

            for (String s : list) {
                quote.appendChild(s);
                quote.appendChild(new Element("br"));
            }
        }
    }

    protected void addScenario(Element root, Scenario scenario) {
        Element divScenario = new Element("div");
        root.appendChild(divScenario);

        if (scenario instanceof ScenarioOutline) {
            addMultiScenario(divScenario, (ScenarioOutline) scenario);
        } else {
            addSingleScenario(divScenario, scenario);
        }
    }

    protected void addMultiScenario(Element root, ScenarioOutline scenario) {
        addExamples(root, scenario, addMacro(root, scenario));
    }

    protected String addMacro(Element root, Scenario scenario) {
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

        addDescription(macro, Type.SCENARIO_OUTLINE, scenario, "h2");
        runList(macro, scenario.getParent().getBackground());
        runList(macro, scenario.getDescription());
        runList(macro, scenario.getParent().getFinallys());

        return name;
    }

    protected void addExamples(Element root, ScenarioOutline scenarioOutline, String name) {
        Element examples = new Element("div");
        root.appendChild(examples);
        {
            addDescription(examples, Type.EXAMPLES, null, "h3");
            addMap(examples, scenarioOutline);
            addIterator(examples, name);
        }
    }

    protected void addMap(Element root, ScenarioOutline scenarioOutline) {
        Element quote = new Element("blockquote");
        root.appendChild(quote);
        {
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
            quote.appendChild(table);
            {
                Element tr = new Element("tr");
                table.appendChild(tr);
                for (String str : scenarioOutline.getNames()) {
                    Element td = new Element("td");
                    td.appendChild(str);
                    tr.appendChild(td);
                }
                for (List<String> e : scenarioOutline.getExamples()) {
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
    }

    protected void addIterator(Element root, String name) {
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
            addCall(iterator, name);
        }
    }

    protected void addCall(Element iterator, String name) {
        Element h4 = new Element("h4");
        h4.appendChild("Example ${index+1}:");
        iterator.appendChild(h4);
        {
            Element quo = new Element("blockquote");
            iterator.appendChild(quo);
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

    public void addSingleScenario(Element root, Scenario scenario) {
        addDescription(root, Type.SCENARIO, scenario, "h2");
        runList(root, scenario.getParent().getBackground());
        runList(root, scenario.getDescription());
        runList(root, scenario.getParent().getFinallys());
    }

    protected void runList(Element root, List<String> list) {
        Element quote = new Element("blockquote");
        String alias;
        try {
            alias = SpecRunnerServices.get(IPluginFactory.class).getAlias(PluginSentence.class);
        } catch (PluginException e) {
            alias = "sentence";
        }
        for (String s : list) {
            Element sentence = new Element("span");
            sentence.appendChild(s);
            sentence.addAttribute(new Attribute("class", alias));
            quote.appendChild(sentence);
            quote.appendChild(new Element("br"));
        }
        root.appendChild(quote);
    }

    @Override
    public String asString(Object obj, Object[] args) {
        if (!(obj instanceof Feature)) {
            return null;
        }
        return null;
    }
}