package org.specrunner.converter.enumeration;

import org.junit.Assert;
import org.junit.Test;
import org.specrunner.converters.ConverterException;
import org.specrunner.converters.core.ConverterEnumValue;
import org.specrunner.util.UtilException;

public class TestConverterEnumValue {

    @Test
    public void testingNull() throws ConverterException {
        ConverterEnumValue ce = new ConverterEnumValue();
        Assert.assertNull(ce.convert(null, null));
        Assert.assertNull(ce.revert(null, null));
    }

    @Test
    public void testingArgs1() throws ConverterException {
        ConverterEnumValue ce = new ConverterEnumValue();
        try {
            ce.convert("any", null);
            Assert.fail("Invalid convertion.");
        } catch (ConverterException e) {
            Assert.assertEquals("Converter requires three arguments: 0) the enum class type (class object or name); 1) enum method name passed to compare values; 2) the enum method to be called for result.", e.getMessage());
        }
    }

    @Test
    public void testingArgs2() throws ConverterException {
        ConverterEnumValue ce = new ConverterEnumValue();
        try {
            ce.convert("any", new String[] {});
            Assert.fail("Invalid convertion.");
        } catch (ConverterException e) {
            Assert.assertEquals("Converter requires three arguments: 0) the enum class type (class object or name); 1) enum method name passed to compare values; 2) the enum method to be called for result.", e.getMessage());
        }
    }

    @Test
    public void testing1() throws ConverterException {
        ConverterEnumValue ce = new ConverterEnumValue();
        for (ItemStatus i : ItemStatus.values()) {
            Object[] args = new Object[] { ItemStatus.class, "name", "ordinal" };
            Object obj = ce.convert(i.name(), args);
            Assert.assertEquals(i.ordinal(), obj);
            Object rev = ce.revert(obj, args);
            Assert.assertEquals(i.name(), rev);
        }
    }

    @Test
    public void testing2() throws ConverterException {
        ConverterEnumValue ce = new ConverterEnumValue();
        for (ItemStatus i : ItemStatus.values()) {
            Object obj = ce.convert(i.ordinal(), new Object[] { ItemStatus.class, "ordinal", "name" });
            Assert.assertEquals(i.name(), obj);
        }
    }

    @Test
    public void testing3() throws ConverterException {
        ConverterEnumValue ce = new ConverterEnumValue();
        for (ItemStatus i : ItemStatus.values()) {
            Object obj = ce.convert(i.name(), new Object[] { ItemStatus.class.getName(), "name", "ordinal" });
            Assert.assertEquals(i.ordinal(), obj);
        }
    }

    @Test
    public void testing4() throws ConverterException {
        ConverterEnumValue ce = new ConverterEnumValue();
        for (ItemStatus i : ItemStatus.values()) {
            Object obj = ce.convert(i.ordinal(), new Object[] { ItemStatus.class.getName(), "ordinal", "name" });
            Assert.assertEquals(i.name(), obj);
        }
    }

    @Test
    public void testingNotEnum() throws ConverterException {
        ConverterEnumValue ce = new ConverterEnumValue();
        try {
            ce.convert("any value", new Object[] { TestConverterEnumValue.class, "name", "ordinal" });
            Assert.fail("Should not convert.");
        } catch (Exception e) {
            Assert.assertEquals("Class '" + TestConverterEnumValue.class.getName() + "' is not an enumeration.", UtilException.unwrapException(e).getMessage());
        }
    }
}