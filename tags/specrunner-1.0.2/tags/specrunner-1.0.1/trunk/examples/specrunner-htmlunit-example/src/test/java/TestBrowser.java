import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestBrowser {

    @Test
    public void rodarBrowser() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/browser.html");
    }

    @Test
    public void rodarBusca() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/busca.html");
    }

    @Test
    public void rodarArvore() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/arvore.html");
    }

    public static void main(String[] args) throws Exception {
        new TestBrowser().rodarBrowser();
    }
}
