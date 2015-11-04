package example.objects.subclass;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestSuper {

    public void callMethodWith(Child c) {
        System.out.println("  ID:" + c.getId());
        System.out.println("NAME:" + c.getName());
    }
}