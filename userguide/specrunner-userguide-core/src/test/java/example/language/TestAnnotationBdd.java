package example.language;

import org.junit.runner.RunWith;
import org.specrunner.junit.concurrent.Concurrent;
import org.specrunner.junit.concurrent.SRRunnerConcurrent;
import org.specrunner.plugins.core.language.Given;
import org.specrunner.plugins.core.language.Then;
import org.specrunner.plugins.core.language.When;

@RunWith(SRRunnerConcurrent.class)
@Concurrent(threads = 4)
public class TestAnnotationBdd {

    private String name;
    private String[] split;

    @Given("nome é $string")
    public void nameIs(String name) {
        this.name = name;
    }

    @When("particionado")
    public void splitted() {
        this.split = name.split(" ");
    }

    @Then("o primeiro nome é $string")
    public boolean firstNameIs(String n) {
        return split[0].equals(n);
    }

    @Then("^o segunssdo nome é $string")
    public boolean lastNameIs(String n) {
        return split[1].equals(n);
    }
}
