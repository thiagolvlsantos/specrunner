package example.sql.value;

public enum ItemStatus {

    ENABLED(0, "Active"), DISABLED(1, "Inative");

    private int code;
    private String description;

    private ItemStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}