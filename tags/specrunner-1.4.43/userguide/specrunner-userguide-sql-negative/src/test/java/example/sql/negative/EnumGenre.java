package example.sql.negative;

public enum EnumGenre {

    MALE('M', "Male"), FEMALE('F', "Female");

    private char code;
    private String description;

    private EnumGenre(char code, String description) {
        this.code = code;
        this.description = description;
    }

    public char getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
