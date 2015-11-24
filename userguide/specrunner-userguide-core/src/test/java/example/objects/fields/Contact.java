package example.objects.fields;

public class Contact {

    private String type;
    private String information;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "Contact [type=" + type + ", information=" + information + "]";
    }
}