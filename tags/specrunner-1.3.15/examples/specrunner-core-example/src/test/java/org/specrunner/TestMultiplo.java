package org.specrunner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;

@RunWith(Parameterized.class)
public class TestMultiplo {

    private String numero;

    public TestMultiplo(String numero) {
        this.numero = numero;
    }

    @Parameters
    public static Collection<Object[]> getNumeros() {
        List<Object[]> i = new LinkedList<Object[]>();
        for (int j = 0; j < 10; j++) {
            i.add(new Object[] { "numero" + j });
        }
        return i;
    }

    @Test
    public void runMultiplo() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        SpecRunnerJUnit.defaultRun("src/test/resources/sources/core.html", "src/test/resources/outcome/example-jetty-" + numero + ".html", cfg);
    }
}