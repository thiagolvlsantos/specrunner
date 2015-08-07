package org.specrunner;

import org.joda.time.DateTime;
import org.junit.Test;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.htmlunit.PluginBrowser;
import org.specrunner.htmlunit.actions.PluginOpen;
import org.specrunner.htmlunit.actions.PluginStartIn;
import org.specrunner.htmlunit.actions.PluginType;
import org.specrunner.htmlunit.assertions.PluginCompareDate;
import org.specrunner.htmlunit.assertions.PluginCompareText;
import org.specrunner.jetty.JettyStringProvider;
import org.specrunner.jetty.PluginStartJetty;
import org.specrunner.junit.SpecRunnerJUnitPlugin;
import org.specrunner.plugins.IPluginGroup;
import org.specrunner.plugins.core.PluginGroupImpl;

public class TestApi {

    @Test
    public void play() throws Exception {
        IExpressionFactory ef = SRServices.getExpressionFactory();
        // date expression elements
        ef.bindValue("pat", "HH:mm").bindValue("pattern", "HH:mm:ss").bindClass("dt", DateTime.class);

        // example
        IPluginGroup group = new PluginGroupImpl();

        PluginStartJetty jetty = new PluginStartJetty();
        jetty.setFile("/jetty.xml");
        group.add(jetty);

        PluginBrowser browser = new PluginBrowser();
        browser.setReuse(Boolean.TRUE);
        group.add(browser);

        // PluginSize size = new PluginSize();
        // size.setWidth(400);
        // size.setHeight(400);
        // group.add(size);

        PluginStartIn start = new PluginStartIn();
        start.setProvider(JettyStringProvider.class.getName());
        group.add(start);

        PluginOpen open = new PluginOpen();
        open.setUrl("/application/text.jsp");
        group.add(open);

        PluginType type = new PluginType();
        type.setEval(true);
        type.setValue("dt.toString(pat)");
        type.getParameters().setParameter("by", "name:txtName", null);
        group.add(type);

        PluginCompareText compare = new PluginCompareText();
        compare.setEval(true);
        compare.setValue("dt.toString(pat)");
        compare.getParameters().setParameter("by", "name:txtName", null);
        group.add(compare);

        type = new PluginType();
        type.setEval(true);
        type.setValue("dt.toString(pattern)");
        type.getParameters().setParameter("by", "name:txtName", null);
        group.add(type);

        PluginCompareDate date = new PluginCompareDate();
        IFeatureManager fm = SRServices.getFeatureManager();
        long MINUTE_PLUS_1_SECOND = 61 * 1000L;
        fm.add(PluginCompareDate.FEATURE_TOLERANCE, MINUTE_PLUS_1_SECOND);
        date.setEval(true);
        date.setFormat("HH:mm:ss");
        date.setValue("dt.toString(pattern)");
        date.getParameters().setParameter("by", "name:txtName", null);
        group.add(date);

        // group.add(new PluginPause());

        SpecRunnerJUnitPlugin.defaultRun(group);
    }
}
