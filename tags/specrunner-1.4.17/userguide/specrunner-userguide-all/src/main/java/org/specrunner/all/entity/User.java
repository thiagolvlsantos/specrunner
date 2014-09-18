package org.specrunner.all.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

    private String usr;
    private String pwd;
    private String name;

    public String getUsr() {
        return usr;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(usr);
    }
}