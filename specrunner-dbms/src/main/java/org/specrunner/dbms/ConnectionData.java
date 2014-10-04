package org.specrunner.dbms;

public class ConnectionData {

    private String schema;
    private String driver;
    private String url;
    private String user;
    private String password;

    public ConnectionData(String schema, String driver, String url, String user, String password) {
        this.schema = schema;
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
