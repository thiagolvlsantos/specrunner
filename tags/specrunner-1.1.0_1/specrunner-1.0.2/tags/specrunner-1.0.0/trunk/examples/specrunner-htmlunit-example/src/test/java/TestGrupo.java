import org.junit.Test;
import org.specrunner.junit.SpecRunnerJUnit;

public class TestGrupo {

    @Test
    public void rodarGrupo() throws Exception {
        SpecRunnerJUnit.defaultRun("src/test/resources/income/grupo.html");
    }
}
