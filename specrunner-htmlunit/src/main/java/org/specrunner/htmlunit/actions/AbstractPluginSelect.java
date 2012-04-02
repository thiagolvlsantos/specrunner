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
package org.specrunner.htmlunit.actions;

import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.context.IContext;
import org.specrunner.htmlunit.AbstractPluginFindSingle;
import org.specrunner.htmlunit.util.WritablePage;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public abstract class AbstractPluginSelect extends AbstractPluginFindSingle implements IAction {

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        if (!(element instanceof HtmlSelect)) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Element " + getFinder().resume(context) + " is not a select is " + element.getClass().getName()), new WritablePage(page));
        } else {
            Node node = context.getNode();
            Nodes nodes = node.query("descendant::li");
            if (nodes.size() == 0) {
                nodes = new Nodes(node);
            }
            HtmlSelect select = (HtmlSelect) element;
            List<HtmlOption> options = select.getOptions();
            int errors = 0;
            for (int i = 0; i < nodes.size(); i++) {
                Node current = nodes.get(i);
                String option = current.getValue();
                boolean success = false;
                for (HtmlOption o : options) {
                    if (option.equals(o.asText())) {
                        doSomething(element, o);
                        success = true;
                        break;
                    }
                }
                if (!success) {
                    result.addResult(Status.FAILURE, context.newBlock(current, this), new PluginException("The option '" + option + "' is missing."));
                    errors++;
                }
            }
            if (errors > 0) {
                result.addResult(Status.FAILURE, context.newBlock(node, this), new PluginException(errors + " option(s) is(are) missing."), new WritablePage(page));
            } else {
                result.addResult(Status.SUCCESS, context.newBlock(node, this));
            }
        }
    }

    /**
     * Perform something on select option.
     * 
     * @param element
     *            The select.
     * @param option
     *            The option.
     */
    protected abstract void doSomething(HtmlElement element, HtmlOption option);
}
