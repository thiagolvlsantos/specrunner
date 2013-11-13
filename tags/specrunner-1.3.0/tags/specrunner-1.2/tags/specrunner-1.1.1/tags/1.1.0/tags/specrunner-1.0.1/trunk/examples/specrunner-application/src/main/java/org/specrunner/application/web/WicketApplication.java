package org.specrunner.application.web;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.specrunner.application.web.pages.SearchPage;

/**
 * Application object for your web application. If you want to run this
 * application without deploying, run the Start class.
 * 
 * @see org.specrunner.application.Start#main(String[])
 */
public class WicketApplication extends WebApplication {

    /**
     * Constructor
     */
    public WicketApplication() {
    }

    @Override
    protected void init() {
        super.init();
        addComponentInstantiationListener(new SpringComponentInjector(this));
        mountBookmarkablePage("home", SearchPage.class);
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<SearchPage> getHomePage() {
        return SearchPage.class;
    }

}
