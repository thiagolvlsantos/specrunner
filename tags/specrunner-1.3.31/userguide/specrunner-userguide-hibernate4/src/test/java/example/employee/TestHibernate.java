package example.employee;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.comparators.core.AbstractComparatorTime;
import org.specrunner.converters.IConverterManager;
import org.specrunner.converters.core.ConverterBooleanTemplate;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.ConcurrentRunner;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.core.AbstractPlugin;

@RunWith(ConcurrentRunner.class)
@Concurrent(threads = 1)
public class TestHibernate {

    @Before
    public void setUpConverters() {
        IConverterManager cf = SRServices.getConverterManager();
        cf.put("bool", new ConverterBooleanTemplate("Sim", "Não"));

        IExpressionFactory ef = SRServices.getExpressionFactory();
        ef.bindClass("dt", DateTime.class);

        IFeatureManager fm = SRServices.getFeatureManager();
        fm.put(AbstractComparatorTime.FEATURE_TOLERANCE, 10000L);

        fm.put(AbstractPlugin.FEATURE_THREADSAFE, Boolean.TRUE);
    }

    public static void run(int index) {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/hibernate.html", "src/test/resources/outcome/hibernate" + index + ".html");
    }

    @Test
    public void rodarHibernate1() throws Exception {
        run(1);
    }

    @Test
    public void rodarHibernate2() throws Exception {
        run(2);
    }

    @Test
    public void rodarHibernate3() throws Exception {
        run(3);
    }

    @Test
    public void rodarHibernate4() throws Exception {
        run(4);
    }

    @Test
    public void rodarHibernate5() throws Exception {
        run(5);
    }

    @Test
    public void rodarHibernate6() throws Exception {
        run(6);
    }

    @Test
    public void rodarHibernate7() throws Exception {
        run(7);
    }

    @Test
    public void rodarHibernate8() throws Exception {
        run(8);
    }

    @Test
    public void rodarHibernate9() throws Exception {
        run(9);
    }
}