/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2016  Thiago Santos

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package example.sql.basic;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.ConcurrentRunner;
import org.specrunner.junit.SpecRunnerJUnit;

//CHECKSTYLE:OFF
@RunWith(ConcurrentRunner.class)
public class TestSql {

    private static final String INCOME = "src/test/resources/income/full/";
    private static final String OUTCOME = "src/test/resources/outcome/full/";

    private void run(String name) {
        SpecRunnerJUnit.defaultRun(INCOME + name, OUTCOME + name);
    }

    @Before
    public void before() {
        IExpressionFactory ief = SRServices.getExpressionFactory();
        String pattern = "yyyy-MM-dd HH:mm:ss.SSSS";
        ief.bindValue("pattern", pattern);
        ief.bindValue("time", "{ts '${dt.toString(pattern)}'}");
        ief.bindClass("dt", DateTime.class);
    }

    @Test
    public void create() {
        run("create.html");
    }

    @Test
    public void createConnection() {
        run("createConnection.html");
    }

    @Test
    public void createProvider() {
        run("createProvider.html");
    }

    @Test
    public void drop() {
        run("drop.html");
    }

    @Test
    public void all() {
        run("all.html");
    }
}
// CHECKSTYLE:ON
