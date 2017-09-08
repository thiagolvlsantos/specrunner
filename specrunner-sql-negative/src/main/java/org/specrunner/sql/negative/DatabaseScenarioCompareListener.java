package org.specrunner.sql.negative;

import org.specrunner.listeners.core.AbstractScenarioWrapperAfterListener;
import org.specrunner.plugins.IPlugin;
import org.specrunner.sql.PluginCompareBase;

public class DatabaseScenarioCompareListener extends AbstractScenarioWrapperAfterListener {

    @Override
    protected Class<? extends IPlugin> getOnEnd() {
        return PluginCompareBase.class;
    }

    @Override
    protected String getOnEndMessage() {
        return "DB_Check";
    }
}
