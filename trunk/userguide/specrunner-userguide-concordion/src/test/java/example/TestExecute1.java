package example;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestExecute1 {

    private Date date;

    public void setCurrentTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mma");
            date = sdf.parse(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getGreeting() {
        return date.getHours() < 12 ? "Good Morning World!" : "Good ...";
    }
}
