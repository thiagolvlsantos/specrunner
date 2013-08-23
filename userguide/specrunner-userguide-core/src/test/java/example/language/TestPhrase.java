package example.language;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.language.Converter;
import org.specrunner.util.converter.impl.ConverterDatePatternArgs;

@RunWith(SRRunner.class)
public class TestPhrase {

    @Before
    public void before() {
        SpecRunnerServices.get(IExpressionFactory.class).bindClass("dt", DateTime.class);
    }

    @ExpectedMessages(messages = { "Falhou!" })
    public void openFileInBrowser(String file) throws Exception {
        String tmp = System.getProperty("user.home");
        if (!tmp.equals(file)) {
            throw new Exception(tmp + " != " + file);
        }
        throw new Exception("Falhou!");
    }

    public void openFileInBrowserString(String file, String other) throws Exception {
        System.out.println("CALLED.1:" + file + "," + other);
    }

    public void openFileInBrowserInteger(String file, Integer other) throws Exception {
        System.out.println("CALLED.2:" + file + "," + other);
    }

    public void openFileInBrowserFloat(String file, Float other) throws Exception {
        System.out.println("CALLED.3:" + file + "," + other);
    }

    public void openFileInBrowserObject(String file, Object other) throws Exception {
        System.out.println("CALLED.4:" + file + "," + other);
    }

    public void openFileInBrowserDate(String file, @Converter(name = "datePatternArgs", args = { "dd/MM/yyyy" }) Date other) throws Exception {
        System.out.println("CALLED.5:" + file + "," + other);
    }

    public void openFileInBrowserDateTyped(String file, @Converter(type = ConverterDatePatternArgs.class, args = { "dd/MM/yyyy" }) Date other) throws Exception {
        System.out.println("CALLED.6:" + file + "," + other);
    }

    @ExpectedMessages(messages = { "Invalid parameter value for argument(1) in public void example.language.TestPhrase.openFileInBrowserDateTypedFail(java.lang.String,java.util.Date) throws java.lang.Exception. Expected class java.util.Date, received: 23/01/2014 of type class java.lang.String" })
    public void openFileInBrowserDateTypedFail(String file, @Converter(args = { "dd/MM/yyyy" }) Date other) throws Exception {
        System.out.println("CALLED.7:" + file + "," + other);
    }
}