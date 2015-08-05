package example.objects;

import org.joda.time.DateTime;

public class DateObject {
    private DateTime date;
    private String text;

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "DateObject [date=" + date + ", text=" + text + "]";
    }
}
