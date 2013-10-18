package example.text;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.language.Sentence;

@RunWith(SRRunner.class)
@Ignore
public class Sum {

    private int sum;

    @Sentence("make $string of")
    public void add(String op) {
        System.out.println("OPERATION:" + op);
        // Assert.assertEquals(dt.getNames().get(0), "Values");
    }

    @Sentence("type")
    public void type(String msg) {
        Assert.assertEquals(msg, "SUM ENTER");
    }

    @Sentence("result is $int")
    public boolean result(int value) {
        Assert.assertEquals(value, sum);
        return true;
    }
}
