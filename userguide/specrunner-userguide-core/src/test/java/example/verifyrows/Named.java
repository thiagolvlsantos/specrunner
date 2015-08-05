package example.verifyrows;

public class Named implements Comparable<Named> {

    private String name;

    public Named(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Named o) {
        return name.compareToIgnoreCase(o.name);
    }
}
