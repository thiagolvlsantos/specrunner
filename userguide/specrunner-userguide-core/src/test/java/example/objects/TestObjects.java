package example.objects;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.annotations.ExpectedMessages;
import org.specrunner.annotations.SRRunnerCondition;
import org.specrunner.annotations.core.SkipFalse;
import org.specrunner.junit.concurrent.Concurrent;
import org.specrunner.junit.concurrent.SRRunnerConcurrent;
import org.specrunner.plugins.core.language.Sentence;

@RunWith(SRRunnerConcurrent.class)
@Concurrent(threads = 4)
@SRRunnerCondition(SkipFalse.class)
@ExpectedMessages({ "Instance 'cama|admin' of 'example.objects.Authorization' not found.", "Instance 'tlvls' of 'example.objects.User' not found." })
public class TestObjects {

    @Before
    public void mapping() {
        SRServices.getExpressionFactory().bindValue("users", "/example/objects/user.html");
        SRServices.getExpressionFactory().bindValue("roles", "/example/objects/role.html");
    }

    public void perform(Authorization aut) {
        System.out.println(aut.getUser() + ":" + aut.getRole() + ":" + aut.getExpiration());
    }

    @Sentence("something else (.+)")
    public boolean other(User u) {
        return "Luciano Barbosa".equals(u.getName());
    }

    @Sentence("Lookup \"(.+)\"")
    public boolean lookup(Authorization aut) {
        return aut.getUser().getName().equals("Thiago Santos") && aut.getRole().getName().equals("Administrator");
    }
}
