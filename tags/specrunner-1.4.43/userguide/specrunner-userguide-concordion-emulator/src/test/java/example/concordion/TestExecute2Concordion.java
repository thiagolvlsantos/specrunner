package example.concordion;

public class TestExecute2Concordion extends TestConcordion {

    public Result split(String fullName) {
        Result result = new Result();
        String[] words = fullName.split(" ");
        result.firstName = words[0];
        result.lastName = words[1];
        return result;
    }
}
