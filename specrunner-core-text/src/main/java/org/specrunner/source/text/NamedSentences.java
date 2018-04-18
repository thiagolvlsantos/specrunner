/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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

import java.util.LinkedList;
import java.util.List;

/**
 * Any object with a name as a group of sentences.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class NamedSentences implements IValidable {

    /**
     * Group name.
     */
    protected String name;
    /**
     * Sentences set.
     */
    protected List<Sentence> sentences = new LinkedList<Sentence>();

    /**
     * The object name.
     * 
     * @param name
     *            The name.
     */
    public NamedSentences(String name) {
        this.name = name;
    }

    /**
     * Get the name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name.
     * 
     * @param name
     *            The name.
     * @return The object itself.
     */
    public NamedSentences setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the list of sentences.
     * 
     * @return The list.
     */
    public List<Sentence> getSentences() {
        return sentences;
    }

    /**
     * Set the sentences list.
     * 
     * @param sentences
     *            The sentences.
     * @return The object itself.
     */
    public NamedSentences setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
        return this;
    }

    /**
     * Add a description.
     * 
     * @param sentence
     *            A sentence.
     * @return The object itself.
     */
    public NamedSentences addSentence(Sentence sentence) {
        this.sentences.add(sentence);
        return this;
    }
}
