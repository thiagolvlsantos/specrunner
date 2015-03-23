package example.converters;

import org.joda.time.DateTime;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestConverters {

    public void print(DateTime date) {
        System.out.println(date);
    }
}
