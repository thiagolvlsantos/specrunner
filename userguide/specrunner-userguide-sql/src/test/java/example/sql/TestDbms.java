/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2013  Thiago Santos

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
package example.sql;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.specrunner.junit.ConcurrentRunner;
import org.specrunner.junit.SpecRunnerJUnit;

//CHECKSTYLE:OFF
@RunWith(ConcurrentRunner.class)
public class TestDbms {

    private static final String INCOME = "src/test/resources/income/dbms/";
    private static final String OUTCOME = "src/test/resources/outcome/dbms/";

    private void run(String name) {
        SpecRunnerJUnit.defaultRun(INCOME + name, OUTCOME + name);
    }

    @Test
    public void create() {
        run("dbms.html");
    }
}
// CHECKSTYLE:ON
