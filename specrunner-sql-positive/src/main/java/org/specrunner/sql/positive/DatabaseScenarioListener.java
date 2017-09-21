package org.specrunner.sql.positive;

import org.specrunner.listeners.core.AbstractScenarioWrapperListener;
import org.specrunner.plugins.IPlugin;

public class DatabaseScenarioListener extends AbstractScenarioWrapperListener {

    @Override
    protected Class<? extends IPlugin> getOnStart() {
        return PluginDbms.class;
    }

    @Override
    protected String getOnStartMessage() {
        return "DB_Clean";
    }

    @Override
    protected Class<? extends IPlugin> getOnEnd() {
        return PluginCompareBase.class;
    }

    @Override
    protected String getOnEndMessage() {
        return "DB_Check";
    }
}
