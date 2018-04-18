/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2018  Thiago Santos

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
package example.objects.verify;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestObjectTree {

    public List<User> users() {
        List<User> result = new LinkedList<User>();
        User u = new User("Sérgio", new LocalDate(1954, 03, 20), Arrays.asList("sergio@yahoo.com", "sls@gmail.com"), new Address("Condado"), null, null);
        result.add(u);

        u = new User("Auricélia", new LocalDate(1953, 12, 23), new LinkedList<String>(), null, new Address[] { new Address("Condado") }, null);
        result.add(u);

        u = new User("Thiago", new LocalDate(), Arrays.asList("thiago@yahoo.com", null, "", null), new Address("Recife"), new Address[] { new Address("Recife") }, Arrays.asList(result.get(0), result.get(1)));
        result.add(u);

        u = new User("", null, null, null, null, null);
        result.add(u);

        return result;
    }

    public User[] call() {
        return new User[] { users().get(0) };
    }
}
