package example.language;

import org.junit.runner.RunWith;
import org.specrunner.annotations.ExpectedMessage;
import org.specrunner.junit.concurrent.Concurrent;
import org.specrunner.junit.concurrent.SRRunnerConcurrent;

@RunWith(SRRunnerConcurrent.class)
@Concurrent(threads = 4)
public class TestSentenceAfter {

    @ExpectedMessage("Falhou!")
    public void openFileInBrowser(String file) throws Exception {
        String tmp = System.getProperty("user.home");
        if (!tmp.equals(file)) {
            throw new Exception(tmp + " != " + file);
        }
        throw new Exception("Falhou!");
    }

    public void openFileInBrowser(String file, String other) throws Exception {
        System.out.println("CALLED.1:" + file + "," + other);
    }

    public void openFileInBrowser(String file, Integer other) throws Exception {
        System.out.println("CALLED.2:" + file + "," + other);
    }

    public void openFileInBrowser(String file, Float other) throws Exception {
        System.out.println("CALLED.3:" + file + "," + other);
    }

    public void openFileInBrowser(String file, Object other) throws Exception {
        System.out.println("CALLED.4:" + file + "," + other);
    }
}
