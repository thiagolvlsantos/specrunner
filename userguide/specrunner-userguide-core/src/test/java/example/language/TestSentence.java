package example.language;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.converters.Converter;
import org.specrunner.converters.core.AbstractConverterTimezone;
import org.specrunner.converters.core.ConverterDatePatternArgs;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.ExpectedMessage;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunnerConcurrent;
import org.specrunner.listeners.core.PauseOnFailureNodeListener;

@RunWith(SRRunnerConcurrent.class)
@Concurrent(threads = 4)
@ExpectedMessages("Method to call not found. Add a 'method' attribute, or extend text to match a regular expression in @Sentence annotations.")
public class TestSentence {

    @Before
    public void before() {
        SRServices.getExpressionFactory().bindClass("dt", DateTime.class);
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.add(PauseOnFailureNodeListener.FEATURE_PAUSE_ON_FAILURE, Boolean.TRUE);
        fm.add(PauseOnFailureNodeListener.FEATURE_SHOW_DIALOG, Boolean.TRUE);
        fm.add(PauseOnFailureNodeListener.FEATURE_CONDITION, Boolean.FALSE);
        fm.add(AbstractConverterTimezone.FEATURE_TIMEZONE, "UTC");

    }

    @ExpectedMessage("Falhou!")
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

    public void openFileInBrowserDate(String file, @Converter(name = "date", args = { "dd/MM/yyyy" }) Date other) throws Exception {
        System.out.println("CALLED.5:" + file + "," + other);
    }

    public void openFileInBrowserDateTyped(String file, @Converter(type = ConverterDatePatternArgs.class, args = { "dd/MM/yyyy" }) Date other) throws Exception {
        System.out.println("CALLED.6:" + file + "," + other);
    }

    public void openFileInBrowserDateTypedFail(String file, @Converter(args = { "dd/MM/yyyy" }) Date other) throws Exception {
        System.out.println("CALLED.7:" + file + "," + other);
    }

    public void anyMethod(int argument) {
        System.out.println("CALLED.8:" + argument);
    }

    public boolean booleanMethod(int argument) {
        return argument == 0;
    }

    @ExpectedMessage("Expected result of 'public boolean example.language.TestSentence.booleanMethodFail(int)' must be 'true'. Received 'false'.")
    public boolean booleanMethodFail(int argument) {
        return booleanMethod(argument);
    }

    public void convertDate(@Converter(args = { "dd/MM/yyyy" }) Date date) {
        System.out.println("CALLED.9: date = " + date);
    }

    public void convertLocalDate(@Converter(args = { "dd/MM/yyyy" }) LocalDate date) {
        System.out.println("CALLED.10: localDate = " + date);
    }

    public void convertDateTime(@Converter(args = { "dd/MM/yyyy HH:mm:ss" }) DateTime date) {
        System.out.println("CALLED.11: dateTime = " + date);
    }

    public void convertBigDecimal(BigDecimal big) {
        System.out.println("CALLED.12: big decimal = " + big);
    }

    public void convertTimestamp(@Converter(args = { "dd/MM/yyyy HH:mm:ss" }) Timestamp other) throws Exception {
        System.out.println("CALLED.13:" + other);
    }

    public void emptyName(String text) {
        System.out.println("CALLED.14:" + text);
        Assert.assertEquals("single argument", text);
    }
}