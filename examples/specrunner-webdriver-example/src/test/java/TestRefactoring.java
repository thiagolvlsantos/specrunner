import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnitPlugin;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.impl.PluginGroupImpl;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.actions.PluginOpen;
import org.specrunner.webdriver.actions.PluginType;
import org.specrunner.webdriver.assertions.PluginCompareText;
import org.specrunner.webdriver.impl.WebDriverFactoryChrome;

public class TestRefactoring {

    @Test
    public void play() throws Exception {
        IPluginGroup group = new PluginGroupImpl();

        PluginBrowser pluginBrowser = new PluginBrowser();
        pluginBrowser.setWebdriverfactory(WebDriverFactoryChrome.class.getName());
        pluginBrowser.setReuse(Boolean.TRUE);
        group.add(pluginBrowser);

        PluginOpen pluginOpen = new PluginOpen();
        pluginOpen.setUrl("http://www.google.com");
        group.add(pluginOpen);

        PluginType type = new PluginType();
        type.setValue("buscar");
        type.getParameters().setParameter("by", "name:q", null);
        group.add(type);

        PluginCompareText compare = new PluginCompareText();
        compare.setValue("Pesquisa googleGGGGG");
        compare.getParameters().setParameter("by", "xpath://title", null);
        group.add(compare);

        SpecRunnerJUnitPlugin.defaultRun(group);
    }
}