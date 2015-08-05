package org.specrunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.specrunner.basic.TestBasic;
import org.specrunner.check.TestCheck;
import org.specrunner.node.TestNode;
import org.specrunner.select.TestSelect;
import org.specrunner.table.TestTable;
import org.specrunner.text.TestText;

@RunWith(Suite.class)
@SuiteClasses({ TestBasic.class, TestCheck.class, TestSelect.class, TestTable.class, TestNode.class, TestText.class })
public class TestAll {
}
