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
package org.specrunner.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.MagicNames;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.specrunner.context.IContext;
import org.specrunner.plugins.ActionType;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.plugins.type.Command;
import org.specrunner.result.IResultSet;
import org.specrunner.result.status.Success;
import org.specrunner.source.ISource;
import org.specrunner.util.UtilLog;

/**
 * Call ant actions.
 * 
 * @author Thiago Santos
 * 
 */
public class PluginAnt extends AbstractPlugin {

    /**
     * Build basedir.
     */
    private String basedir;
    /**
     * Build file.
     */
    private String file;
    /**
     * Build target.
     */
    private String target;
    /**
     * Debug level.
     */
    private Integer debug = Project.MSG_INFO;

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
        project.addBuildListener(logger());
        String msg = "base dir=" + project.getUserProperty(MagicNames.PROJECT_BASEDIR) + ", file=" + project.getUserProperty(MagicNames.ANT_FILE);
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
            if (UtilLog.LOG.isDebugEnabled()) {
                UtilLog.LOG.debug("Ant call error. " + msg, e);
            }
            throw new PluginException(e);
        }
    }

    /**
     * Creates the logger.
     * 
     * @return The logger.
     */
    protected DefaultLogger logger() {
        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(debug);
        return consoleLogger;
    }
}