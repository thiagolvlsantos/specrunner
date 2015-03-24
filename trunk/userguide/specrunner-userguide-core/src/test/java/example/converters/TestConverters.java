package example.converters;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestConverters {

    public void print(DateTime date) {
        System.out.println("DT: " + date);
    }

    public void printDate(LocalDate date) {
        System.out.println("LD:" + date);
    }
}
