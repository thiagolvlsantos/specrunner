package example.map;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.core.language.Sentence;

@RunWith(SRRunner.class)
public class TestMap {

    @Sentence("$a plus $b is $c")
    public boolean sum(int a, int b, int c) {
        Assert.assertEquals(c, a + b);
        return true;
    }
}
