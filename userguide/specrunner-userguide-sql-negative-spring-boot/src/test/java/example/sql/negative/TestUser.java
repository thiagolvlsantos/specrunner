package example.sql.negative;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

import example.sql.negative.service.User;

@RunWith(SRRunner.class)
public class TestUser {

    public void callUser(User user) {
        Assert.assertEquals("Thiago", user.getName());
    }
}
