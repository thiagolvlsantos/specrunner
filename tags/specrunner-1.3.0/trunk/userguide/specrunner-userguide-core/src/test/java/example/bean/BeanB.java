package example.bean;

public class BeanB extends BeanA {

    @Override
    public String name() {
        return getClass().getSimpleName();
    }
}
