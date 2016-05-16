package example.repeat;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestRepeat {

    private List<Object> objs = new LinkedList<Object>();

    public void add(String value, LocalDate date) {
        objs.add(date);
    }

    public boolean size(int size) {
        return objs.size() == size;
    }
}
