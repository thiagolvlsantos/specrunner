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
package org.specrunner.plugins.impl.macro;

import nu.xom.Element;
import nu.xom.Node;

import org.specrunner.context.IContext;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.impl.AbstractPluginScoped;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.UtilNode;

/**
 * The plugin which creates a macro. To defined a macro just add the class
 * 'macro' to a tag, and add a 'name' attribute to allow further references to
 * this macro.
 * 
 * @author Thiago Santos.
 * 
 */
public class PluginMacro extends AbstractPluginScoped {

    /**
     * CSS of macro defined.
     */
    public static final String CSS_DEFINED = "macro_defined";

    /**
     * Set if the macro should be called on creation time also.
     */
    private boolean run;
    /**
     * Set if a macro should be global to the current specification.
     */
    private boolean global;

    /**
     * Says if the macro must be execute on definition time. Default is 'false'.
     * 
     * @return true, if perform on definition also, false, otherwise.
     */
    public boolean isRun() {
        return run;
    }

    /**
     * Set run behavior.
     * 
     * @param run
     *            The new run status.
     */
    public void setRun(boolean run) {
        this.run = run;
    }

    /**
     * Says is the plugin should be placed in global context or local context.
     * Default value is 'false'.
     * 
     * @return True, of global, false, otherwise.
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * Sets global flag.
     * 
     * @param global
     *            If define global as global.
     */
    public void setGlobal(boolean global) {
        this.global = global;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Object macro = context.getByName(getName());
        if (macro != null) {
            throw new PluginException("Macro name already used in '" + (macro instanceof Node ? ((Node) macro).toXML() : macro) + "'");
        }
        Element ele = (Element) context.getNode();
        if (isGlobal()) {
            saveGlobal(context, getName(), ele.copy());
        } else {
            saveLocal(context, getName(), ele.copy());
        }
        UtilNode.appendCss(ele, CSS_DEFINED);
        return (run ? ENext.DEEP : ENext.SKIP);
    }
}