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
package org.specrunner.browser.assertions;

import java.util.List;

import nu.xom.Node;
import nu.xom.Nodes;

import org.specrunner.SpecRunnerServices;
import org.specrunner.browser.AbstractPluginFindSingle;
import org.specrunner.browser.util.WritablePage;
import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;
import org.specrunner.util.aligner.IStringAligner;
import org.specrunner.util.aligner.IStringAlignerFactory;
import org.specrunner.util.aligner.impl.DefaultAlignmentException;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public class PluginCheckSelection extends AbstractPluginFindSingle implements IAssertion {

    private Boolean content;
    private Boolean selection;

    /**
     * Check the content of a select.
     * 
     * @return true, to check content.
     */
    public Boolean getContent() {
        return content;
    }

    public void setContent(Boolean content) {
        this.content = content;
    }

    /**
     * Check the selection of a select.
     * 
     * @return true, to check selected items.
     */
    public Boolean getSelection() {
        return selection;
    }

    public void setSelection(Boolean selection) {
        this.selection = selection;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        if (content == null && selection == null) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Set plugin content or selection."));
            return;
        }
        int error = checkContent(context, result, client, page, element);
        error = error + checkSelection(context, result, client, page, element);
        if (error == 0) {
            result.addResult(Status.SUCCESS, context.peek());
        }
    }

    protected int checkContent(IContext context, IResultSet result, WebClient client, Page page, HtmlElement element) throws PluginException {
        int error = 0;
        if (content != null) {
            if (!(element instanceof HtmlSelect)) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element on " + getFinder().resume(context) + " is not a select is " + element.getClass().getName()), new WritablePage(page));
                error = 1;
            } else {
                Node node = context.getNode();
                Nodes nodes = node.query("descendant::li");
                HtmlSelect select = (HtmlSelect) element;
                List<HtmlOption> options = select.getOptions();
                error = testList(context, result, page, nodes, options, content);
            }
        }
        return error;
    }

    protected int checkSelection(IContext context, IResultSet result, WebClient client, Page page, HtmlElement element) throws PluginException {
        int error = 0;
        if (selection != null) {
            if (!(element instanceof HtmlSelect)) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("Element on " + getFinder().resume(context) + " is not a select is " + element.getClass().getName()), new WritablePage(page));
                error = 1;
            } else {
                Node node = context.getNode();
                Nodes nodes = node.query("descendant::li");
                if (nodes.size() == 0) {
                    nodes = new Nodes(node);
                }
                HtmlSelect select = (HtmlSelect) element;
                List<HtmlOption> options = select.getSelectedOptions();
                error = testList(context, result, page, nodes, options, content);
            }
        }
        return error;
    }

    @SuppressWarnings("serial")
    protected int testList(IContext context, IResultSet result, Page page, Nodes nodes, List<HtmlOption> options, Boolean content) {
        int error = 0;
        String[] expecteds = new String[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            expecteds[i] = nodes.get(i).getValue();
        }
        String[] receiveds = new String[options.size()];
        for (int i = 0; i < receiveds.length; i++) {
            receiveds[i] = options.get(i).asText();
        }
        if (expecteds.length != receiveds.length) {
            result.addResult(Status.FAILURE, context.peek(), new PluginException("Number of itens (" + receiveds.length + ") in " + (content != null && content ? "content" : "selected") + " is different from expected ones (" + expecteds.length + ")."), new WritablePage(page));
            error = 1;
        } else {
            for (int i = 0; i < expecteds.length; i++) {
                if (!expecteds[i].equals(receiveds[i])) {
                    IStringAligner al = SpecRunnerServices.get(IStringAlignerFactory.class).align(expecteds[i], receiveds[i]);
                    result.addResult(Status.FAILURE, context.newBlock(nodes.get(i), this), new DefaultAlignmentException(al) {
                        @Override
                        public String getMessage() {
                            return "Selection options are diferent (" + super.getMessage() + ").";
                        }
                    });
                    error++;
                }
            }
            if (error > 0) {
                result.addResult(Status.FAILURE, context.peek(), new PluginException("The element(s) expected and received do(es) not match."), new WritablePage(page));
            }
        }
        return error;
    }
}
