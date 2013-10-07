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

public class PluginAnt extends AbstractPlugin {

    private String dir;
    private String file;
    private String target;
    private Integer debug = Project.MSG_INFO;

    @Override
    public ActionType getActionType() {
        return Command.INSTANCE;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Integer getDebug() {
        return debug;
    }

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
        if (dir == null) {
            ISource current = context.getCurrentSource();
            File source = current.getFile();
            buildFile = new File(source.getParentFile(), file);
        } else {
            File parent = new File(dir);
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

    protected DefaultLogger logger() {
        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(System.err);
        consoleLogger.setOutputPrintStream(System.out);
        consoleLogger.setMessageOutputLevel(debug);
        return consoleLogger;
    }
}