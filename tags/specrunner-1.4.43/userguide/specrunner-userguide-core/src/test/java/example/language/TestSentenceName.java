package example.language;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestSentenceName {

    public URL loadUrlWith(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
