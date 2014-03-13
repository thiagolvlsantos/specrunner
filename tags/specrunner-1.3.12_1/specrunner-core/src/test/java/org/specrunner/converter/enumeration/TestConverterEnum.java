package org.specrunner.converter.enumeration;

import org.junit.Assert;
import org.junit.Test;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.core.ConverterEnum;
import org.specrunner.util.ExceptionUtil;

public class TestConverterEnum {

    @Test
    public void testingNull() throws ConverterException {
        ConverterEnum ce = new ConverterEnum();
        Assert.assertNull(ce.convert(null, null));
    }

    @Test
    public void testingArgs1() throws ConverterException {
        ConverterEnum ce = new ConverterEnum();
        try {
            ce.convert("any", null);
            Assert.fail("Invalid convertion.");
        } catch (ConverterException e) {
            Assert.assertEquals("Converter requires two arguments: 0) the enum class type (class object or name); 1) enum method name passed to compare values.", e.getMessage());
        }
    }

    @Test
    public void testingArgs2() throws ConverterException {
        ConverterEnum ce = new ConverterEnum();
        try {
            ce.convert("any", new String[] {});
            Assert.fail("Invalid convertion.");
        } catch (ConverterException e) {
            Assert.assertEquals("Converter requires two arguments: 0) the enum class type (class object or name); 1) enum method name passed to compare values.", e.getMessage());
        }
    }

    @Test
    public void testing1() throws ConverterException {
        ConverterEnum ce = new ConverterEnum();
        for (ItemStatus i : ItemStatus.values()) {
            Object obj = ce.convert(i.name(), new Object[] { ItemStatus.class, "name" });
            Assert.assertEquals(i, obj);
        }
    }

    @Test
    public void testing2() throws ConverterException {
        ConverterEnum ce = new ConverterEnum();
        for (ItemStatus i : ItemStatus.values()) {
            Object obj = ce.convert(i.ordinal(), new Object[] { ItemStatus.class, "ordinal" });
            Assert.assertEquals(i, obj);
        }
    }

    @Test
    public void testing3() throws ConverterException {
        ConverterEnum ce = new ConverterEnum();
        for (ItemStatus i : ItemStatus.values()) {
            Object obj = ce.convert(i.name(), new Object[] { ItemStatus.class.getName(), "name" });
            Assert.assertEquals(i, obj);
        }
    }

    @Test
    public void testing4() throws ConverterException {
        ConverterEnum ce = new ConverterEnum();
        for (ItemStatus i : ItemStatus.values()) {
            Object obj = ce.convert(i.ordinal(), new Object[] { ItemStatus.class.getName(), "ordinal" });
            Assert.assertEquals(i, obj);
        }
    }

    @Test
    public void testingNotEnum() throws ConverterException {
        ConverterEnum ce = new ConverterEnum();
        try {
            ce.convert("any value", new Object[] { TestConverterEnum.class, "name" });
            Assert.fail("Should not convert.");
        } catch (Exception e) {
            Assert.assertEquals("Class '" + TestConverterEnum.class.getName() + "' is not an enumeration.", ExceptionUtil.unwrapException(e).getMessage());
        }
    }
}