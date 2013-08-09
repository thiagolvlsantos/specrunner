package example.slim;

import java.util.Date;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestSlim {

    public long a;
    private double b;
    private double result;

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public void action(String action) {
        switch (action.charAt(0)) {
        case '+':
            result = a + b;
            break;
        case '-':
            result = a - b;
            break;
        case '*':
            result = a * b;
            break;
        case '/':
            result = a / b;
            break;
        case '%':
            result = a % b;
            break;
        }
        a = 0;
        b = 0;
    }

    public double result() {
        return result;
    }

    public Object parameter(Object content) {
        return content;
    }

    public void time(Date data) {
        System.out.println(data);
    }
}