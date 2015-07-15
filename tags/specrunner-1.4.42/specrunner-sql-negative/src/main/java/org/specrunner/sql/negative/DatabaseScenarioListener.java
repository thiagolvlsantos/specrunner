package org.specrunner.sql.negative;

import org.specrunner.listeners.core.AbstractScenarioWrapperListener;
import org.specrunner.plugins.IPlugin;
import org.specrunner.sql.PluginCompareBase;

public class DatabaseScenarioListener extends AbstractScenarioWrapperListener {

    @Override
    protected Class<? extends IPlugin> getOnStart() {
        return PluginDbms.class;
    }

    @Override
    protected String getOnStartMessage() {
        return "Database cleanup.";
    }

    @Override
    protected Class<? extends IPlugin> getOnEnd() {
        return PluginCompareBase.class;
    }

    @Override
    protected String getOnEndMessage() {
        return "Database checkup.";
    }
}