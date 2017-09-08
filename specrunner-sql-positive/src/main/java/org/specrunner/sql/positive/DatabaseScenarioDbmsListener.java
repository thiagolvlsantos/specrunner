package org.specrunner.sql.positive;

import org.specrunner.listeners.core.AbstractScenarioWrapperBeforeListener;
import org.specrunner.plugins.IPlugin;

public class DatabaseScenarioDbmsListener extends AbstractScenarioWrapperBeforeListener {

    @Override
    protected Class<? extends IPlugin> getOnStart() {
        return PluginDbms.class;
    }

    @Override
    protected String getOnStartMessage() {
        return "DB_Clean";
    }
}
