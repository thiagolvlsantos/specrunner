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
package org.specrunner.runner.impl;

import java.util.LinkedList;
import java.util.List;

import org.specrunner.plugins.ActionType;
import org.specrunner.runner.IRunner;

/**
 * Default runner implementation.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class AbstractRunner implements IRunner {

    /**
     * List of disabled aliases.
     */
    protected List<String> disabledAliases;
    /**
     * List of enabled aliases.
     */
    protected List<String> enabledAliases;

    /**
     * List of disabled types.
     */
    protected List<? extends ActionType> disabledTypes;
    /**
     * List of enabled types.
     */
    protected List<? extends ActionType> enabledTypes;

    @Override
    public void setDisabledAliases(List<String> disabledAliases) {
        if (disabledAliases == null) {
            this.disabledAliases = null;
        } else {
            this.disabledAliases = new LinkedList<String>();
            for (String s : disabledAliases) {
                if (s != null) {
                    this.disabledAliases.add(s.toLowerCase());
                }
            }
        }
    }

    @Override
    public List<String> getDisabledAliases() {
        return disabledAliases;
    }

    @Override
    public void setEnabledAliases(List<String> enabledAliases) {
        if (enabledAliases == null) {
            this.enabledAliases = null;
        } else {
            this.enabledAliases = new LinkedList<String>();
            for (String s : enabledAliases) {
                if (s != null) {
                    this.enabledAliases.add(s.toLowerCase());
                }
            }
        }
    }

    @Override
    public List<String> getEnabledAliases() {
        return enabledAliases;
    }

    @Override
    public void setDisabledTypes(List<? extends ActionType> disabledTypes) {
        if (disabledTypes == null) {
            this.disabledTypes = null;
        } else {
            this.disabledTypes = new LinkedList<ActionType>(disabledTypes);
        }
    }

    @Override
    public List<? extends ActionType> getDisabledTypes() {
        return disabledTypes;
    }

    @Override
    public void setEnabledTypes(List<? extends ActionType> enabledTypes) {
        if (enabledTypes == null) {
            this.enabledTypes = null;
        } else {
            this.enabledTypes = new LinkedList<ActionType>(enabledTypes);
        }
    }

    @Override
    public List<? extends ActionType> getEnabledTypes() {
        return enabledTypes;
    }
}