package th.example;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
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
import org.specrunner.util.converter.impl.ConverterBooleanTemplate;

@RunWith(ConcurrentRunner.class)
@Concurrent(threads = 3)
public class TestHibernate {

    public static Object lock = new Object();
    public static Boolean done = false;

    @BeforeClass
    public static void antes() {
        synchronized (lock) {
            if (!done) {
                // pause();
                done = true;
            }
        }
    }

    @Before
    public void setUpConverters() {
        IConverterManager cf = SpecRunnerServices.get(IConverterManager.class);
        cf.put("bool", new ConverterBooleanTemplate("Sim", "Não"));

        IExpressionFactory ief = SpecRunnerServices.get(IExpressionFactory.class);
        ief.bindClass("dt", DateTime.class);

        IFeatureManager fm = SpecRunnerServices.getFeatureManager();
        fm.put(AbstractComparatorTime.FEATURE_TOLERANCE, 10000L);

        fm.put(AbstractPlugin.FEATURE_THREADSAFE, Boolean.TRUE);

        // List<ReportPart> list = new LinkedList<ReportPart>();
        // list.add(new ReportPart("STATUS", StatusComparator.get()));
        // fh.put(AbstractReport.FEATURE_PARTS, list);

        // IListenerManager lm = SpecRunnerServices.get(IListenerManager.class);
        // lm.add(new AbstractNodeListener() {
        // @Override
        // public void reset() {
        // }
        //
        // @Override
        // public ENext onBefore(Node node, IContext context, IResultSet result)
        // {
        // return node instanceof Element && "table".equalsIgnoreCase(((Element)
        // node).getQualifiedName()) ? ENext.SKIP : ENext.DEEP;
        // }
        //
        // @Override
        // public String getName() {
        // return "ignorarTabelas";
        // }
        // });

        // exemplo de habilitar/desabilitar plugins
        // fh.add(IRunner.FEATURE_ENABLED_ALIASES, Arrays.asList("global",
        // "hbmcfg"));
        // fh.add(IRunner.FEATURE_DISABLED_ALIASES, Arrays.asList("global",
        // "hbmcfg"));
    }

    private static void pause() {
        try {
            System.out.println("Enter");
            System.in.read(new byte[12]);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void run(int index) {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/hibernate.html", "src/test/resources/outcome/hibernate" + index + ".html");
        // pause();
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