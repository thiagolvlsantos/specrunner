package org.specrunner;

import org.joda.time.DateTime;
import org.junit.Before;
import org.specrunner.annotations.Configuration;
import org.specrunner.comparators.core.AbstractComparatorTime;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.htmlunit.impl.FinderXPath;

public class AbstractTest {

    @Configuration
    public void onConfigure(IConfiguration cfg) {
        System.out.println("Running with SRRunner.class");
    }

    @Before
    public void onBefore() {
        IExpressionFactory ef = SRServices.getExpressionFactory();

        // date expression elements
        ef.bindValue("pattern", "HH:mm:ss").bindClass("dt", DateTime.class);

        // longer tolerance
        SRServices.getFeatureManager().add(AbstractComparatorTime.FEATURE_TOLERANCE, 60000L);

        // XPATH search strategy example
        String args0 = "//*[starts-with(@id,'{0}')] | //*[starts-with(@name,'{0}')] | //*[starts-with(@value,'{0}')]";
        String args1 = "//*[starts-with(@id,'{1}')] | //*[starts-with(@name,'{1}')] | //*[starts-with(@value,'{1}')]";
        FinderXPath.get().addStrategy("starts_new", args0 + "|" + args1);
    }
}
