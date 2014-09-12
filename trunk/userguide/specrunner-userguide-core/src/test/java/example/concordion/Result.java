package example.concordion;

public class Result implements Comparable<Result> {
    public String firstName;
    public String lastName;

    @Override
    public int compareTo(Result o) {
        return firstName.compareToIgnoreCase(o.lastName);
    }
}