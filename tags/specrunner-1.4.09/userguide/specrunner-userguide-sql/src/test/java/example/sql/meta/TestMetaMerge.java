package example.sql.meta;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;
import org.specrunner.SRServices;
import org.specrunner.sql.meta.Column;
import org.specrunner.sql.meta.Schema;
import org.specrunner.sql.meta.Table;
import org.specrunner.util.xom.INodeHolderFactory;

public class TestMetaMerge {

    @Test
    public void schemaMerge() throws Exception {
        Schema old = new Schema().setName("PRO").setAlias("Projects");
        Schema next = new Schema().setName("PRR").setAlias("Projects set");
        old.merge(next);
        Assert.assertEquals(old.getAlias(), next.getAlias());
        Assert.assertEquals(old.getName(), next.getName());
    }

    @Test
    public void schemaMergeTable() throws Exception {
        Schema old = new Schema().setName("PRO").setAlias("Projects");
        Table t1 = new Table().setName("TAS").setAlias("Tasks");
        old.add(t1);
        Schema next = new Schema().setName("PRR").setAlias("Projects set");
        Table t2 = new Table().setName("RES").setAlias("Resources");
        old.add(t2);
        old.merge(next);

        Table tmp = old.getName("TAS");
        Assert.assertEquals(tmp.getAlias(), t1.getAlias());
        tmp = old.getName("RES");
        Assert.assertEquals(tmp.getAlias(), t2.getAlias());
    }

    @Test
    public void schemaMergeTableColumns() throws Exception {
        Schema old = new Schema().setName("PRO").setAlias("Projects");
        Table t1 = new Table().setName("TAS").setAlias("Tasks");
        old.add(t1);
        Column col11 = new Column().setName("TAS_NAME").setAlias("Name").setKey(true);
        t1.add(col11);
        Column col12 = new Column().setName("TAS_DATE").setAlias("Date").setDate(true);
        t1.add(col12);

        Schema next = new Schema().setName("PRR").setAlias("Projects set");
        Table t2 = new Table().setName("RES").setAlias("Resources");
        old.add(t2);
        Element element = new Element("span");
        element.appendChild("0");
        Column col21 = new Column().setName("RES_INDEX").setAlias("Index").setDefaultValue(SRServices.get(INodeHolderFactory.class).create(element));
        t2.add(col21);
        Table t3 = new Table().setName("TAS").setAlias("Tasks new");
        next.add(t3);
        Column col31 = new Column().setName("TAS_DESCRIPTION").setAlias("Description").setDate(true);
        t1.add(col31);

        old.merge(next);

        Table tmp = old.getName("TAS");
        Assert.assertEquals(tmp.getAlias(), "tasks new");
        Column c1 = tmp.getName("TAS_NAME");
        Assert.assertEquals(c1.getAlias(), "name");
        Assert.assertEquals(c1.isKey(), true);
        Column c2 = tmp.getName("TAS_DATE");
        Assert.assertEquals(c2.getAlias(), "date");
        Column c3 = tmp.getName("TAS_DESCRIPTION");
        Assert.assertEquals(c3.getAlias(), "description");

        tmp = old.getName("RES");
        Assert.assertEquals(tmp.getAlias(), "resources");
        Column c4 = tmp.getName("RES_INDEX");
        Assert.assertEquals(c4.getAlias(), "index");
        // Assert.assertEquals(c4.getDefaultValue(), 0);
    }
}