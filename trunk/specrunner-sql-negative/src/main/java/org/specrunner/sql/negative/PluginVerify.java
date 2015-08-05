package org.specrunner.sql.negative;

import org.specrunner.sql.PluginPrepare;

public class PluginVerify extends PluginPrepare {

    public PluginVerify() {
        setDatasource("referenceConnection");
        setDatabase("referenceDatabase");
    }
}
