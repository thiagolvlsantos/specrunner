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

import java.io.IOException;

import org.specrunner.context.IContext;
import org.specrunner.plugins.PluginException;
import org.specrunner.result.IResultSet;
import org.specrunner.result.Status;

import com.gargoylesoftware.htmlunit.SgmlPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.impl.SelectableTextInput;

public class PluginType extends AbstractPluginKeys {

    /**
     * Set the append mode.
     */
    private Boolean append = false;
    /**
     * Write to a given text position.
     */
    private Integer position;
    /**
     * The select option.
     */
    private Boolean select = true;

    /**
     * Get the append mode.
     * 
     * @return true, if append is on, false, otherwise. Default is false.
     */
    public Boolean getAppend() {
        return append;
    }

    /**
     * To append text, without clear, set this true. Default is <b>false</b>.
     * 
     * @param append
     *            true, to append, false, otherwise.
     */
    public void setAppend(Boolean append) {
        this.append = append;
    }

    /**
     * Get the position to type text.
     * 
     * @return The text position.
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * Set the position to insert text.
     * 
     * @param position
     *            Index position to write.
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * Gets the select status. If select is true, the content is selected before
     * writing.
     * 
     * @return The select status.
     */
    public Boolean getSelect() {
        return select;
    }

    /**
     * ets the select status.
     * 
     * @param select
     *            The status.
     */
    public void setSelect(Boolean select) {
        this.select = select;
    }

    @Override
    protected void process(IContext context, IResultSet result, WebClient client, SgmlPage page, HtmlElement element) throws PluginException {
        Object tmp = getValue(getValue() != null ? getValue() : context.getNode().getValue(), true, context);
        String value = String.valueOf(tmp);
        try {
            if (element instanceof SelectableTextInput) {
                SelectableTextInput sel = (SelectableTextInput) element;
                if (select) {
                    sel.select();
                } else {
                    sel.setSelectionStart(0);
                    sel.setSelectionEnd(0);
                }
                if (append != null && append) {
                    String text = element.asText();
                    sel.setSelectionStart(text.length());
                    sel.setSelectionEnd(text.length());
                }
                if (position != null) {
                    String text = element.asText();
                    sel.setSelectionStart(Math.max(0, Math.min(position, text.length())));
                    sel.setSelectionEnd(Math.max(0, Math.min(position, text.length())));
                }
            }
            if (shiftkey != null || ctrlkey != null || altkey != null) {
                if (value.length() == 1) {
                    element.type(value.charAt(0), shiftkey != null ? shiftkey : false, ctrlkey != null ? ctrlkey : false, altkey != null ? altkey : false);
                } else {
                    element.type(value, shiftkey != null ? shiftkey : false, ctrlkey != null ? ctrlkey : false, altkey != null ? altkey : false);
                }
            } else {
                if (value.length() == 1) {
                    element.type(value.charAt(0));
                } else {
                    element.type(value);
                }
            }
            result.addResult(Status.SUCCESS, context.peek());
        } catch (IOException e) {
            result.addResult(Status.FAILURE, context.peek(), e);
        }
    }
}