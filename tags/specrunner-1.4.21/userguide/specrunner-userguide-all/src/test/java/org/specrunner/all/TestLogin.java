package org.specrunner.all;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.SpecRunnerException;
import org.specrunner.all.entity.User;
import org.specrunner.all.entity.UserDao;
import org.specrunner.configuration.IConfiguration;
import org.specrunner.context.IContext;
import org.specrunner.context.IModel;
import org.specrunner.expressions.IExpressionFactory;
import org.specrunner.junit.Configuration;
import org.specrunner.junit.SRRunner;

@RunWith(SRRunner.class)
public class TestLogin {

    @Configuration
    public void cfg(IConfiguration cfg) {
        // cfg.add(PluginBrowser.FEATURE_WEBDRIVER_FACTORY,
        // WebDriverFactoryChrome.class.getName());
    }

    @Before
    public void before() {
        IExpressionFactory ef = SRServices.getExpressionFactory();
        ef.bindValue("baseUrl", "/application");
        ef.bindModel("longText", new IModel<String>() {
            @Override
            public String getObject(IContext context) throws SpecRunnerException {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 30; i++) {
                    sb.append(i % 10);
                }
                return sb.toString();
            }

        });
        ef.bindModel("userIsAdmin", new IModel<Boolean>() {
            @Override
            public Boolean getObject(IContext context) throws SpecRunnerException {
                User user = UserDao.current();
                System.out.println("us:" + user.getName() + " is admin = " + user.isAdmin());
                return user.isAdmin();
            }
        });
    }
}