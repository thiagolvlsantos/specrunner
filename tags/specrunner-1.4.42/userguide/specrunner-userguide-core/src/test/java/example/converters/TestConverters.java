package example.converters;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestConverters {

    public void printDate(Date date) {
        System.out.println("DATE: " + date);
    }

    public void printTime(Timestamp date) {
        System.out.println("TIME:" + date);
    }

    public void printDt(DateTime date) {
        System.out.println("DT: " + date);
    }

    public void printLd(LocalDate date) {
        System.out.println("LD:" + date);
    }
}