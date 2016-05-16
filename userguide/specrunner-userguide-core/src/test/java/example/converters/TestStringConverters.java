package example.converters;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.specrunner.converters.Converter;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestStringConverters {

    public void str(String str) {
        System.out.println(str);
        Assert.assertEquals(" thiago    ", str);
    }

    public void strTrim(@Converter(name = "st") String str) {
        System.out.println(str);
        Assert.assertEquals("thiago", str);
    }

    public void strNormalized(@Converter(name = "sn") String str) {
        System.out.println(str);
        Assert.assertEquals("ThiagoSantos", str);
    }

    public void upper(@Converter(name = "upper") String str) {
        System.out.println(str);
        Assert.assertEquals("THIAGO", str);
    }

    public void upperTrim(@Converter(name = "upperTrim") String str) {
        System.out.println(str);
        Assert.assertEquals("THIAGO", str);
    }

    public void lower(@Converter(name = "lower") String str) {
        System.out.println(str);
        Assert.assertEquals("thiago", str);
    }

    public void lowerTrim(@Converter(name = "lowerTrim") String str) {
        System.out.println(str);
        Assert.assertEquals("thiago", str);
    }

    public void camelCase(@Converter(name = "camelCase") String str) {
        System.out.println(str);
        Assert.assertEquals("testCamel", str);
    }
}
