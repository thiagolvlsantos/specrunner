package example.concordion;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.runner.RunWith;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestVerifyRows {

    private List<String> usernamesInSystem = new LinkedList<String>();

    public void setUpUser(String username) {
        usernamesInSystem.add(username);
    }

    public Iterable<String> getSearchResultsFor(String searchString) {
        SortedSet<String> matches = new TreeSet<String>();
        for (String username : usernamesInSystem) {
            if (username.contains(searchString)) {
                matches.add(username);
            }
        }
        return matches;
    }
}
