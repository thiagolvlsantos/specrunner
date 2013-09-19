package example.objects;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SpecRunnerServices;
import org.specrunner.converters.Converter;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.ExpectedMessages;
import org.specrunner.junit.SRRunner;
import org.specrunner.plugins.impl.language.Sentence;

@RunWith(SRRunner.class)
@ExpectedMessages(messages = { "Instance 'tlvls' of 'example.objects.User' not found." })
public class TestObjects {

    @Before
    public void mapping() {
        SpecRunnerServices.get(IExpressionFactory.class).bindValue("users", "/example/objects/user.html");
        SpecRunnerServices.get(IExpressionFactory.class).bindValue("roles", "/example/objects/role.html");
    }

    public void perform(@Converter(name = "object", resultType = Authorization.class) Authorization aut) {
        System.out.println(aut.getUser() + ":" + aut.getRole() + ":" + aut.getExpiration());
    }

    @Sentence("something else (.+)")
    public boolean other(@Converter(name = "object", resultType = User.class) User u) {
        return "Luciano Barbosa".equals(u.getName());
    }
}