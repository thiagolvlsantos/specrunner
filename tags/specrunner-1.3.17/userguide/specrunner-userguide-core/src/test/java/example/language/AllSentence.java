package example.language;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestAnnotation.class, TestCalculator.class, TestSentence.class, TestSentenceAfter.class })
public class AllSentence {

}
