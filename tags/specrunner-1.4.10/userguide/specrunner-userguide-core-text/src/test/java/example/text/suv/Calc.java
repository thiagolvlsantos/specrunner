package example.text.suv;

import java.util.LinkedList;
import java.util.List;

public class Calc {

    private List<Integer> numbers = new LinkedList<Integer>();

    public void clear() {
        numbers.clear();
    }

    public void enter(Integer n) {
        numbers.add(n);
    }

    public void add() {
        Integer r = numbers.get(0) + numbers.get(1);
        numbers.clear();
        numbers.add(r);
    }

    public void subtract() {
        Integer r = numbers.get(0) - numbers.get(1);
        numbers.clear();
        numbers.add(r);
    }

    public Integer result() {
        return numbers.get(0);
    }
}
