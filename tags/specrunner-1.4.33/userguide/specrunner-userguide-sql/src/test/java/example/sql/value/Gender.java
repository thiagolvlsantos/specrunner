package example.sql.value;

public enum Gender {

    MALE('M', "Male"), FEMALE('F', "Female"), OTHER('O', "Other");

    private char code;
    private String description;

    private Gender(char code, String description) {
        this.code = code;
        this.description = description;
    }

    public char getCode() {
        return code;
    }

    public void setCode(char code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}