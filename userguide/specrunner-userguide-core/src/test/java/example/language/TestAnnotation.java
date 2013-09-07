package example.language;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.language.Sentence;

@RunWith(SRRunner.class)
public class TestAnnotation {

    @Sentence("nothing")
    public void call() {
        System.out.println("Empty");
    }

    @Sentence("press (\\d+) on calc")
    public void call(int value) {
        System.out.println("VALUE:" + value);
    }

    @Sentence("call (\\d+) on calc, and (.+)\\.")
    public void otherCall(int value, String arg) {
        System.out.println("OTHER_VALUE:" + value);
        System.out.println("OTHER_ARG:" + arg);
    }

    @Sentence("result (\\d+) of (\\d+) is (\\d+)\\.")
    public boolean otherCall(int a, int b, int c) {
        System.out.println("ASSERTION (a+b)=c");
        return a + b == c;
    }
}