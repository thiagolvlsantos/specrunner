package example.variables;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestDefine {

    private int local = 0;
    private int global = 0;
    private int set = 0;
    private int call = 0;

    public int local() {
        System.out.println("local:" + local);
        return ++local;
    }

    public boolean verifyLocal(int c) {
        return c == 1;
    }

    public int global() {
        System.out.println("global:" + global);
        return ++global;
    }

    public boolean verifyGlobal(int c) {
        return c == 1;
    }

    public int set() {
        System.out.println("set:" + set);
        return ++set;
    }

    public boolean verifySet(int c) {
        return c == 1;
    }

    public int call() {
        System.out.println("call:" + call);
        return ++call;
    }

    public boolean verifyCall(int c) {
        return c == 1;
    }
}