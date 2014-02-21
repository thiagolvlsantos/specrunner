package org.specrunner.all.entity;

public class UserDao {

    private static User current;

    public static User lookup(User user) {
        boolean match = user.getUsr() == null ? user.getPwd() == null : user.getUsr().equals(user.getPwd());
        if (!match) {
            throw new RuntimeException("Invalid user/password.");
        }
        user.setName("John " + user.getUsr());
        current = user;
        return user;
    }

    public static User current() {
        return current;
    }
}
