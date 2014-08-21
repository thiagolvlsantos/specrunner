package example.concordion;

import org.junit.runner.RunWith;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;
import org.specrunner.junit.SRRunnerOptions;
import org.specrunner.plugins.core.elements.AbstractPluginResource;
import org.specrunner.result.IResultSet;
import org.specrunner.source.resource.IResourceManager;
import org.specrunner.util.UtilLog;

@RunWith(SRRunner.class)
@SRRunnerOptions(pipeline = "specrunner_fast.xml")
public abstract class TestConcordion {

    @Configuration
    public void prepare(IConfiguration cfg) {
        cfg.add(AbstractPluginResource.FEATURE_SAVE, UtilLog.LOG.isDebugEnabled());
        cfg.add(IResourceManager.FEATURE_ADD_RESOURCES, UtilLog.LOG.isDebugEnabled());
        cfg.add(IResultSet.FEATURE_RECORD_SUCCESS, UtilLog.LOG.isDebugEnabled());
    }
}