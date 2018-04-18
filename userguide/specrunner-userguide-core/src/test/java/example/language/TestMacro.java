package example.language;

import org.junit.runner.RunWith;
import org.specrunner.junit.concurrent.Concurrent;
import org.specrunner.junit.concurrent.SRRunnerConcurrent;

@RunWith(SRRunnerConcurrent.class)
@Concurrent(threads = 4)
public class TestMacro extends TestAnnotationBdd {

}
