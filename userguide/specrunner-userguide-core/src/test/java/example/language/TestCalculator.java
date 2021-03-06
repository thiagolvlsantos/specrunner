package example.language;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.junit.concurrent.Concurrent;
import org.specrunner.junit.concurrent.SRRunnerConcurrent;
import org.specrunner.plugins.core.language.Given;
import org.specrunner.plugins.core.language.Synonyms;
import org.specrunner.plugins.core.language.Then;
import org.specrunner.plugins.core.language.When;

@RunWith(SRRunnerConcurrent.class)
@Concurrent(threads = 4)
public class TestCalculator {

    private List<Double> list = new LinkedList<Double>();
    private String operation;

    @Given("input (-?\\d+.?\\d+?)")
    @Synonyms({ "type (-?\\d+.?\\d+?)", "write (-?\\d+.?\\d+?)" })
    public void arg(Double value) {
        list.add(value);
    }

    @When("press (.+)")
    @Synonyms(value = { "type OPeration (.+)" }, options = Pattern.MULTILINE)
    public void action(String act) {
        operation = act;
    }

    @Then("result is (\\d+.?\\d+?)")
    @Synonyms({ "expected value (-?\\d+.?\\d+?)" })
    public boolean result(Double value) {
        Double d = null;
        if ("add".equals(operation)) {
            d = list.get(0) + list.get(1);
        }
        if ("minus".equals(operation)) {
            d = list.get(0) - list.get(1);
        }
        list.clear();
        Assert.assertEquals(value, d);
        return true;
    }
}
