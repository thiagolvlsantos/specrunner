package example.reader;

import java.io.InputStream;
import java.io.Reader;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.comparators.core.ComparatorMd5;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestReaderUrl {

    public void compareAnd(Object o1, Object o2) {
        Assert.assertTrue(new ComparatorMd5().compare(o1, o2) == 0);
    }

    public void differentAnd(InputStream o1, InputStream o2) {
        Assert.assertFalse(new ComparatorMd5().compare(o1, o2) == 0);
    }

    public void readerCompareAnd(Object o1, Object o2) {
        Assert.assertTrue(new ComparatorMd5().compare(o1, o2) == 0);
    }

    public void readerDifferentAnd(Reader o1, Reader o2) {
        Assert.assertFalse(new ComparatorMd5().compare(o1, o2) == 0);
    }
}
