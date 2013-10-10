package example.language;

import org.junit.runner.RunWith;
import org.specrunner.junit.Concurrent;
import org.specrunner.junit.SRRunnerConcurrent;
import org.specrunner.plugins.impl.language.Sentence;

@RunWith(SRRunnerConcurrent.class)
@Concurrent(threads = 4)
public class TestAnnotation {

    @Sentence("nothing")
    public void call() {
        System.out.println("Empty");
    }

    @Sentence("press $int on calc")
    public void call(int value) {
        System.out.println("VALUE:" + value);
    }

    @Sentence("call $boolean on calc, and $string\\.")
    public void otherCall(int value, String arg) {
        System.out.println("OTHER_VALUE:" + value);
        System.out.println("OTHER_ARG:" + arg);
    }

    @Sentence("result of (\\d+) plus (\\d+) is (\\d+)\\.")
    public boolean otherCall(int a, int b, int c) {
        System.out.println("ASSERTION (a+b)=c");
        return a + b == c;
    }
}