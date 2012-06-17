package org.specrunner.application;

import junit.framework.TestCase;

import org.apache.wicket.util.tester.WicketTester;
import org.specrunner.application.web.WicketApplication;
import org.specrunner.application.web.pages.SearchPage;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage extends TestCase {
    private WicketTester tester;

    @Override
    public void setUp() {
        tester = new WicketTester(new WicketApplication());
    }

    public void testRenderMyPage() {
        // start and render the test page
        tester.startPage(SearchPage.class);

        // assert rendered page class
        tester.assertRenderedPage(SearchPage.class);

        // assert rendered label component
        tester.assertLabel("message", "If you see this message wicket is properly configured and running");
    }
}
