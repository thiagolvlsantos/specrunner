package com.example;

import org.junit.Test;
import org.specrunner.SpecRunnerServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.impl.elements.AbstractPluginResource;

public class TestGreeter {

    @Test
    public void testGreeter() {
        IConfiguration cfg = SpecRunnerServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(AbstractPluginResource.FEATURE_SAVE, false);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/HelloWorld.html", cfg);
    }

    @Test
    public void testGreeterImages() {
        SpecRunnerServices.get(IFeatureManager.class).put(AbstractPluginResource.FEATURE_SAVE, true);
        SpecRunnerJUnit.defaultRun("src/test/resources/income/HelloWorld.html", "src/test/resources/outcome/HelloWorldImages.html");
    }
}
