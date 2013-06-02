package example.sql;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class PrintListener implements TestExecutionListener {

    @Override
    public void afterTestClass(TestContext arg0) throws Exception {
        System.out.println("afterTestClass:" + arg0);
    }

    @Override
    public void afterTestMethod(TestContext arg0) throws Exception {
        System.out.println("afterTestMethod:" + arg0);
    }

    @Override
    public void beforeTestClass(TestContext arg0) throws Exception {
        System.out.println("beforeTestClass:" + arg0);
    }

    @Override
    public void beforeTestMethod(TestContext arg0) throws Exception {
        System.out.println("beforeTestMethod:" + arg0);
    }

    @Override
    public void prepareTestInstance(TestContext arg0) throws Exception {
        System.out.println("prepareTestInstance:" + arg0);
    }

}
