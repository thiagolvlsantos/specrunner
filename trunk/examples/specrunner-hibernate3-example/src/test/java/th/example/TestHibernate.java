package th.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.hibernate.PluginConfiguration;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.ConcurrentJunitRunner;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.util.comparer.impl.AbstractComparatorTime;
import org.specrunner.util.converter.IConverterManager;

@RunWith(ConcurrentJunitRunner.class)
@Concurrent(threads = 6)
public class TestHibernate {

    @Before
    public void setUpConverters() {
        IConverterManager cf = SpecRunnerServices.get(IConverterManager.class);
        cf.bind("bool", new ConverterSimNao());

        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.put(AbstractComparatorTime.FEATURE_TOLERANCE, 10000L);

        fh.put(PluginConfiguration.FEATURE_THREADSAFE, Boolean.TRUE);
    }

    protected void run(int index) {
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