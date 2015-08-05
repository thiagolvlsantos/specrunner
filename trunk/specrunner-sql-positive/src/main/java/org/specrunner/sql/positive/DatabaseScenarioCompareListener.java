package org.specrunner.sql.positive;

import org.specrunner.listeners.core.AbstractScenarioWrapperAfterListener;
import org.specrunner.plugins.IPlugin;

public class DatabaseScenarioCompareListener extends AbstractScenarioWrapperAfterListener {

    @Override
    protected Class<? extends IPlugin> getOnEnd() {
        return PluginCompareBase.class;
    }

    @Override
    protected String getOnEndMessage() {
        return "Database checkup.";
    }
}
