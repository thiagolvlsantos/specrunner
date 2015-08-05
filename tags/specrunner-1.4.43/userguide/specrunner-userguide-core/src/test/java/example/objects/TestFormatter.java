package example.objects;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestFormatter {

    public void call(DateObject date) {
        System.out.println(date);
    }
}
