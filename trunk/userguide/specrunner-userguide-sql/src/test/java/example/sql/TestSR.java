package example.sql;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nu.xom.Node;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.IPluginFactory;
import org.specrunner.plugins.impl.factories.PluginFactoryCSS;
import org.specrunner.plugins.impl.factories.PluginFactoryElement;

@RunWith(SRRunner.class)
public class TestSR {

    @Before
    public void antes() throws Exception {
        System.out.println("antes");
        SpecRunnerServices.get(IExpressionFactory.class).bindClass("dt", Date.class);
        SpecRunnerServices.get(IPluginFactory.class).bind(PluginFactoryCSS.KIND, "meu", new MeuPlugin());
        SpecRunnerServices.get(IFeatureManager.class).add(MeuPlugin.FEATURE_PADRAO, "não");
        SpecRunnerServices.get(IPluginFactory.class).bind(PluginFactoryElement.KIND, "outro", new OutroPlugin());
    }

    public void test(int index, Node node) {
        System.out.println("NODE(" + index + "):" + node.toXML());
    }

    public String imprime(Node node) {
        String xml = node.toXML();
        System.out.println(xml);
        return xml;
    }

    public String call(Date date) {
        return "funciona " + date;
    }

    public String greeting(String nome) {
        return "Oi " + nome + "!";
    }

    public String split() {
        return null;
    }

    public boolean bool(int index) {
        return index % 2 == 0;
    }

    public List<?> chamar(Date dt) {
        return Arrays.asList("a", "b", "c");
    }

    @After
    public void depois() {
        System.out.println("depois");
    }
}
