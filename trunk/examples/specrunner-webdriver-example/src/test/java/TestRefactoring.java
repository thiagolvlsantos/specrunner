import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnitPlugin;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.impl.PluginGroupImpl;
import org.specrunner.webdriver.PluginBrowser;
import org.specrunner.webdriver.actions.PluginOpen;
import org.specrunner.webdriver.actions.PluginType;
import org.specrunner.webdriver.assertions.PluginCompare;
import org.specrunner.webdriver.impl.WebDriverFactoryChrome;

public class TestRefactoring {

    @Test
    public void play() throws Exception {
        IPluginGroup group = new PluginGroupImpl();

        group.add(new PluginBrowser().set("webdriverfactory", WebDriverFactoryChrome.class.getName()).set("reuse", Boolean.TRUE));
        group.add(new PluginOpen().set("url", "http://www.google.com"));

        PluginType type = new PluginType();
        type.setValue("buscar");
        type.getParameters().setParameter("by", "name:q");
        group.add(type);

        PluginCompare compare = new PluginCompare();
        compare.setValue("Pesquisa googleGGGGG");
        compare.getParameters().setParameter("by", "xpath://title");
        group.add(compare);

        SpecRunnerJUnitPlugin.defaultRun(group);
    }
}