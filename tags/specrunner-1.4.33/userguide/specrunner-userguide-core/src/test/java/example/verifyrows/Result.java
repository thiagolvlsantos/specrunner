package example.verifyrows;

public class Result implements Comparable<Result> {
    public Named firstName;
    public Named lastName;

    public Named getFirstName() {
        return firstName;
    }

    public void setFirstName(Named firstName) {
        this.firstName = firstName;
    }

    public Named getLastName() {
        return lastName;
    }

    public void setLastName(Named lastName) {
        this.lastName = lastName;
    }

    @Override
    public int compareTo(Result o) {
        return firstName.compareTo(o.lastName);
    }
}