package org.specrunner.all;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.specrunner.all.entity.User;

@SuppressWarnings("serial")
public class UserSession extends WebSession {

    private User user;

    public UserSession(Request request) {
        super(request);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}