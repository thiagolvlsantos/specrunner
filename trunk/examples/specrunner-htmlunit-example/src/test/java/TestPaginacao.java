import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestPaginacao {

    @Test
    public void rodarBrowser() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/paginacao.html");
    }
}
