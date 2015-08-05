package example.text;

import java.util.List;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.core.language.Sentence;
import org.specrunner.source.text.DataTable;

@RunWith(SRRunner.class)
// @Ignore
public class Sum {

    private int data;

    public void cleanStatus() {
        data = 0;
    }

    @Sentence("make a $string of")
    public void op(String op, DataTable dt) {
        Assert.assertEquals(dt.getNames().get(0), "Values");
        if ("sum".equals(op)) {
            for (List<String> e : dt.getExamples()) {
                data += Integer.parseInt(e.get(0));
            }
        } else if ("multiplication".equals(op)) {
            data = 1;
            for (List<String> e : dt.getExamples()) {
                data *= Integer.parseInt(e.get(0));
            }
        }
    }

    @Sentence("type")
    public void message(String msg) {
        Assert.assertEquals(msg.trim(), "SUM ENTER");
    }

    public void andCall(String msg) {
        Assert.assertEquals(msg.trim(), "ENTER");
    }

    @Sentence("result is $int")
    public boolean result(int value) {
        Assert.assertEquals(value, data);
        return true;
    }
}
