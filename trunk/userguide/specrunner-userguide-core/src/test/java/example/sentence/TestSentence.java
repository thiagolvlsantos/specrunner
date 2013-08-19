package example.sentence;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestSentence {

    public void openFileInBrowser(String file) throws Exception {
        String tmp = System.getProperty("user.home");
        if (!tmp.equals(file)) {
            throw new Exception(tmp + " != " + file);
        }
    }
}