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
package org.specrunner.source.text;

/**
 * A valid sentence.
 * 
 * @author Thiago Santos
 * 
 */
public class Sentence implements IValidable {

    /**
     * The text of sentence.
     */
    protected String text;
    /**
     * The data of the sentence, if any.
     */
    protected DataTable data;
    /**
     * The message of the sentence, if any.
     */
    protected String message;

    /**
     * Creates a sentence with a given text.
     * 
     * @param text
     *            The text.
     */
    public Sentence(String text) {
        this.text = text;
    }

    /**
     * Get the sentence text.
     * 
     * @return The text.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the sentence text.
     * 
     * @param text
     *            The text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * check if a sentence has data table.
     * 
     * @return true, if it has, false, otherwise.
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * Get data information.
     * 
     * @return The data.
     */
    public DataTable getData() {
        return data;
    }

    /**
     * Set the data table.
     * 
     * @param data
     *            The table.
     */
    public void setData(DataTable data) {
        this.data = data;
    }

    /**
     * Check if a sentence has a message associated.
     * 
     * @return true, if it has, false, otherwise.
     */
    public boolean hasMessage() {
        return message != null;
    }

    /**
     * Get the message content.
     * 
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message content.
     * 
     * @param message
     *            The message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String validate() {
        StringBuilder sb = new StringBuilder();
        if (text == null) {
            sb.append("Invalid sentence with null text.\n");
        }
        if (text != null && text.endsWith(":") && !hasData() && !hasMessage()) {
            sb.append("Sentences terminated by ':' expect data table or message values.");
        }
        if (hasData()) {
            sb.append(data.validate());
        }
        return sb.toString();
    }
}