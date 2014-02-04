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
package org.specrunner.plugins.impl.text;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.data.PluginDataList.Pair;
import org.specrunner.result.IResultSet;
import org.specrunner.util.UtilEvaluator;
import org.specrunner.util.UtilLog;

/**
 * Replace list values.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginReplacerList extends PluginReplacerItem {

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Object map = context.getByName("item_list");
        if (map == null) {
            return ENext.DEEP;
        }

        Node node = context.getNode();
        String text = node.getValue();
        String replaced = replaceList(text, (Pair) map);
        if (UtilLog.LOG.isDebugEnabled() && !text.equals(replaced)) {
            UtilLog.LOG.debug("replacer_list_before>" + text);
            UtilLog.LOG.debug("replacer_list_after>" + replaced + "." + replaced.getClass());
        }
        replaceText(node, context, text, replaced);
        return ENext.DEEP;
    }

    public String replaceList(String text, Pair map) throws PluginException {
        String result = text;
        int pos1 = text.indexOf(UtilEvaluator.START_DATA);
        int pos2 = text.indexOf(UtilEvaluator.END, pos1 + UtilEvaluator.START_DATA.length());
        while (pos1 >= 0 & pos2 > pos1) {
            // escape character
            if (pos1 > 0 && text.charAt(pos1 - 1) == UtilEvaluator.ESCAPE) {
                pos1 = text.indexOf(UtilEvaluator.START_DATA, pos2 + 1);
                pos2 = text.indexOf(UtilEvaluator.END, pos1 + UtilEvaluator.START_DATA.length());
                continue;
            }
            String content = text.substring(pos1 + UtilEvaluator.START_DATA.length(), pos2);
            String name = UtilEvaluator.START_DATA + content + UtilEvaluator.END;
            if (content.equals("node")) {
                result = UtilEvaluator.replace(result, name, dump((Element) map.getSource()));
            } else {
                result = UtilEvaluator.replace(result, name, map.getData());
            }
            pos1 = text.indexOf(UtilEvaluator.START_DATA, pos2 + 1);
            pos2 = text.indexOf(UtilEvaluator.END, pos1 + UtilEvaluator.START_DATA.length());
        }
        return result;
    }

    public static String dump(Node node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node);
        return sb.toString();
    }

    public static String dump(Element node) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(node.getQualifiedName());
        for (int i = 0; i < node.getAttributeCount(); i++) {
            Attribute att = node.getAttribute(i);
            sb.append(" " + att.getQualifiedName() + " = '" + att.toXML() + "'");
        }
        sb.append(">");
        for (int i = 0; i < node.getChildCount(); i++) {
            Node n = node.getChild(i);
            if (n instanceof Element) {
                sb.append(dump((Element) n));
            } else if (n instanceof Text) {
                sb.append(dump((Text) n));
            } else {
                sb.append(dump(n));
            }
        }
        sb.append("</");
        sb.append(node.getQualifiedName());
        sb.append(">");
        return sb.toString();
    }

    public static String dump(Text node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getValue());
        return sb.toString();
    }
}
