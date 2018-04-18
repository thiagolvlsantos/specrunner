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
package org.specrunner.ant;

import java.io.File;
import java.io.IOException;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.core.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.source.ISource;
import org.specrunner.source.resource.EType;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.source.resource.ResourceException;
import org.specrunner.util.UtilLog;
import org.specrunner.util.xom.UtilNode;

/**
 * Call ant actions.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginAnt extends AbstractPlugin {

    /**
     * Serial for executions.
     */
    protected static ThreadLocal<Integer> serial = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    /**
     * Build basedir.
     */
    protected String basedir;
    /**
     * Build file.
     */
    protected String file;
    /**
     * Build target.
     */
    protected String target;
    /**
     * Debug level.
     */
    protected Integer debug = Project.MSG_INFO;

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    /**
     * Get Ant basedir.
     * 
     * @return Null, of not set, the Ant basedir otherwise.
     */
    public String getBasedir() {
        return basedir;
    }

    /**
     * Set basedir.
     * 
     * @param basedir
     *            The basedir.
     */
    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    /**
     * Get build file.
     * 
     * @return The build file.
     */
    public String getFile() {
        return file;
    }

    /**
     * Set build file.
     * 
     * @param file
     *            The file.
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Get the build target.
     * 
     * @return The target.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Set build target. If none is set, the 'default' task will be selected.
     * 
     * @param target
     *            The target.
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * get de debug level.
     * 
     * @return The debug level.
     */
    public Integer getDebug() {
        return debug;
    }

    /**
     * Set the debug level.
     * 
     * @param debug
     *            The level.
     */
    public void setDebug(Integer debug) {
        this.debug = debug;
    }

    @Override
    public void doEnd(IContext context, IResultSet result) throws PluginException {
        if (file == null) {
            throw new PluginException("Missing 'file' attribute. Set 'file' to choose file, and optionaly 'dir' to set base dir.");
        }
        Project project = new Project();
        File buildFile = null;
        if (basedir == null) {
            ISource current = context.getCurrentSource();
            File source = current.getFile();
            buildFile = new File(source.getParentFile(), file);
        } else {
            File parent = new File(basedir);
            project.setUserProperty(MagicNames.PROJECT_BASEDIR, parent.getAbsolutePath());
            buildFile = new File(parent, file);
        }
        project.setUserProperty(MagicNames.ANT_FILE, buildFile.getAbsolutePath());
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference(MagicNames.REFID_PROJECT_HELPER, helper);
        AntLogger logger = newLogger();
        logger.setMessageOutputLevel(debug);
        project.addBuildListener(logger);
        String msg = "base dir=" + project.getUserProperty(MagicNames.PROJECT_BASEDIR) + ", file=" + project.getUserProperty(MagicNames.ANT_FILE);
        Throwable error = null;
        try {
            project.fireBuildStarted();
            project.init();
            helper.parse(project, buildFile);
            String tmp = target == null ? project.getDefaultTarget() : target;
            msg += ", target=" + tmp;
            project.executeTarget(tmp);
            project.fireBuildFinished(null);
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Success: " + msg);
            }
            result.addResult(Success.INSTANCE, context.peek(), "Ant call success: " + msg);
        } catch (BuildException e) {
            project.fireBuildFinished(e);
            error = e;
        }
        Node node = context.getNode();
        UtilNode.appendCss(node, "sr_antcall");
        ParentNode parent = node.getParent();
        int index = parent.indexOf(node);

        Integer n = serial.get() + 1;
        serial.set(n);
        if (node instanceof Element) {
            ((Element) node).addAttribute(new Attribute("antcall", String.valueOf(n)));
        }
        try {
            Element ele = new Element("pre");
            UtilNode.appendCss(ele, "sr_antlog");
            if (error == null) {
                ele.addAttribute(new Attribute("style", "display:none;"));
            }
            ele.addAttribute(new Attribute("antlog", String.valueOf(n)));
            ele.appendChild(logger.getContent());
            parent.insertChild(ele, index + 1);
            // add style and script resources
            addResources(context);
        } catch (IOException e) {
            throw new PluginException(e);
        }
        if (error != null) {
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Ant call error. " + msg, error);
            }
            throw new PluginException(error);
        }
    }

    /**
     * Creates the logger.
     * 
     * @return The logger.
     */
    protected AntLogger newLogger() {
        return new AntLogger();
    }

    /**
     * Add complementary resources.
     * 
     * @param context
     *            The context.
     * @throws PluginException
     *             On adding errors.
     */
    protected void addResources(IContext context) throws PluginException {
        IResourceManager manager = context.getCurrentSource().getManager();
        try {
            manager.addCss("css/sr_ant.css", true, EType.BINARY);
        } catch (ResourceException e) {
            throw new PluginException(e);
        }
        try {
            manager.addJs("js/sr_ant.js", true, EType.BINARY);
        } catch (ResourceException e) {
            throw new PluginException(e);
        }
    }
}
