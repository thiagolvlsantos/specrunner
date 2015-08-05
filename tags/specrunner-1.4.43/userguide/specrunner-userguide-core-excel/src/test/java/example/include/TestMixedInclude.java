package example.include;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.PluginKind;
import org.specrunner.plugins.core.include.PluginInclude;
import org.specrunner.plugins.core.language.Sentence;

@RunWith(SRRunner.class)
public class TestMixedInclude {

    private int counter = 0;

    @Before
    public void adjust() throws PluginException {
        // show expanded tree
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.add(PluginInclude.FEATURE_EXPANDED, true);

        // plugin factory or factories
        IPluginFactory factory = SRServices.get(IPluginFactory.class);

        // create a CSS type for increase.
        PluginInclude increase = new PluginInclude();
        increase.setDir("src/test/java/example/include");
        increase.setHref("increase.html");
        factory.bind(PluginKind.CSS, "incr", increase);

        // create a element type for decrease.
        PluginInclude decrease = new PluginInclude();
        decrease.setHref("excel/decrease.xlsx");
        factory.bind(PluginKind.ELEMENT, "decr", decrease);
    }

    public void inc() {
        counter++;
    }

    @Sentence("decrease")
    public void dec() {
        counter--;
    }

    public boolean countIs(int expected) {
        Assert.assertEquals(expected, counter);
        return true;
    }
}
