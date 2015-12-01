package example.values;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestValue {

    private String data;

    public String data() {
        return "00001";
    }

    public String getData() {
        return "007";
    }

    public void setData(String data) {
        this.data = data;
    }
}