package example.concordion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestExecute1Concordion extends TestConcordion {

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
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR) < 12 ? "Good Morning World!" : "Good ...";
    }
}
