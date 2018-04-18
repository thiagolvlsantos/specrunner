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
package org.specrunner.plugins.core.flow;

import org.specrunner.SRServices;
import org.specrunner.context.IContext;
import org.specrunner.features.IFeatureManager;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.ENext;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPluginNamed;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.util.xom.UtilNode;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

/**
 * Performs the branch selection.
 * 
 * @author Thiago Santos
 * 
 */
public abstract class PluginIfBranch extends AbstractPluginNamed {

    /**
     * CSS style for selected branch.
     */
    public static final String CSS_SELECTED = "selected";
    /**
     * CSS style for unselected branch.
     */
    public static final String CSS_RELEGATED = "relegated";

    /**
     * Feature to set invalid branch.
     */
    public static final String FEATURE_HIDE = PluginIfBranch.class.getName() + ".hide";
    /**
     * Set hide on invalid branch.
     */
    protected Boolean hide;

    /**
     * Gets the hide status. Default is 'true'.
     * 
     * @return true, to hide, false, otherwise.
     */
    public Boolean getHide() {
        return hide;
    }

    /**
     * Sets the hide status of unselected branches.
     * 
     * @param hide
     *            true, to hide, false, otherwise.
     */
    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    @Override
    public void initialize(IContext context) throws PluginException {
        super.initialize(context);
        if (hide == null) {
            IFeatureManager fm = SRServices.getFeatureManager();
            fm.set(FEATURE_HIDE, this);
        }
    }

    @Override
    public ENext doStart(IContext context, IResultSet result) throws PluginException {
        Boolean ifResult = PluginIf.getTest(context, getName());
        Boolean condition = expected() == ifResult;
        encapsule(context, condition);
        return condition ? ENext.DEEP : ENext.SKIP;
    }

    /**
     * Wrap the node with 'selected' or 'unselected' flag, and hide or not the
     * unselected branch.
     * 
     * @param context
     *            The context.
     * @param condition
     *            The condition.
     */
    protected void encapsule(IContext context, Boolean condition) {
        Node node = context.getNode();
        ParentNode parent = node.getParent();
        int index = parent.indexOf(node);
        Element enc = new Element("span");
        if (!condition) {
            UtilNode.setIgnore(enc);
        }
        UtilNode.appendCss(node, condition ? CSS_SELECTED : CSS_RELEGATED);
        node.detach();
        if (condition || (hide != null && !hide)) {
            enc.appendChild(node);
            parent.insertChild(enc, index);
        }
    }

    /**
     * Return the expected branch condition.
     * 
     * @return true, for 'do', false, for 'else'.
     */
    protected abstract boolean expected();
}
