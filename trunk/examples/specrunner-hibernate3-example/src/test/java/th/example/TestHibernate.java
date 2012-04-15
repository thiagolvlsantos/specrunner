package th.example;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.ConcurrentRunner;
import org.specrunner.junit.SpecRunnerJUnit;
import org.specrunner.plugins.impl.AbstractPlugin;
import org.specrunner.util.comparer.impl.AbstractComparatorTime;
import org.specrunner.util.converter.IConverterManager;

@RunWith(ConcurrentRunner.class)
@Concurrent(threads = 6)
public class TestHibernate {

    @Before
    public void setUpConverters() {
        pause();
        IConverterManager cf = SpecRunnerServices.get(IConverterManager.class);
        cf.bind("bool", new ConverterSimNao());

        IExpressionFactory ief = SpecRunnerServices.get(IExpressionFactory.class);
        ief.bindPredefinedClass("dt", DateTime.class);

        IFeatureManager fh = SpecRunnerServices.get(IFeatureManager.class);
        fh.put(AbstractComparatorTime.FEATURE_TOLERANCE, 10000L);

        fh.put(AbstractPlugin.FEATURE_THREADSAFE, Boolean.TRUE);
    }

    private void pause() {
        // try {
        // System.out.println("Enter");
        // System.in.read(new byte[12]);
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    protected void run(int index) {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/hibernate.html", "src/test/resources/outcome/hibernate" + index + ".html");
        pause();
    }

    @Test
    public void rodarHibernateMap() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/hibernateMap.html", "src/test/resources/outcome/hibernateMap.html");
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