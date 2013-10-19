package example.include;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.PluginException;
import org.specrunner.plugins.impl.factories.PluginFactoryCSS;
import org.specrunner.plugins.impl.factories.PluginFactoryElement;
import org.specrunner.plugins.impl.include.PluginInclude;

@RunWith(SRRunner.class)
public class TestInclude {

    private int counter = 0;

    @Before
    public void adjust() throws PluginException {
        // create a CSS type for increase.
        PluginInclude increase = new PluginInclude();
        increase.setDir("src/test/java/example/include");
        increase.setHref("increase.html");
        SpecRunnerServices.get(IPluginFactory.class).bind(PluginFactoryCSS.KIND, "incr", increase);

        // create a element type for decrease.
        PluginInclude decrease = new PluginInclude();
        decrease.setHref("decrease.html");
        SpecRunnerServices.get(IPluginFactory.class).bind(PluginFactoryElement.KIND, "decr", decrease);
    }

    public void inc() {
        counter++;
    }

    public void dec() {
        counter--;
    }

    public boolean countIs(int expected) {
        Assert.assertEquals(expected, counter);
        return true;
    }
}