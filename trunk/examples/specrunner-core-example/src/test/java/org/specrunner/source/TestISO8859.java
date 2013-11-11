package org.specrunner.source;

import java.net.URI;
import java.net.URLEncoder;

import org.junit.Test;
import org.specrunner.SRServices;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.configuration.IConfigurationFactory;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.impl.include.PluginInclude;

public class TestISO8859 extends TstPai {

    @Test
    public void rodarTeste() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/sources/Inserção Example/custom.html", "src/test/resources/outcome/Inserção Example/custom.html");
    }

    @Test
    public void rodarGoogle() throws Exception {
        IConfiguration cfg = SRServices.get(IConfigurationFactory.class).newConfiguration();
        cfg.add(PluginInclude.FEATURE_DEPTH, 3);
        SpecRunnerJUnit.defaultRun("http://www.cin.ufpe.br/~tlvls/center.htm", cfg);
    }

    public static void main(String[] args) throws Exception {
        String str = "Inserção Example/custom.html";
        str = URLEncoder.encode(str, "UTF-8");
        System.out.println(new URI(str).toASCIIString());
    }
}
