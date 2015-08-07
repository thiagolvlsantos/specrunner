package example.bean;

public class BeanA {

    private String msg;

    public BeanA() {
    }

    public BeanA(String msg) {
        this.msg = msg;
    }

    public String name() {
        return getClass().getSimpleName() + msg;
    }
}
