package example.values;

import org.junit.runner.RunWith;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
@ExpectedMessages(value = { "Invalid argument[0]='25/10/2015' of type 'class java.lang.String' for method: public void example.values.TestNullVarargs.dateList(example.values.TestNullVarargs$Type[])." })
public class TestNullVarargs {

    public static enum Type {
        IN, OUT;
    }

    public void dateList(Type... types) {
        System.out.println(types);
    }
}
