package org.specrunner.application.util;

import org.apache.commons.dbcp.BasicDataSource;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
        BasicDataSource basic = (BasicDataSource) applicationContext.getBean("dataSource");
        String url = basic.getUrl();
        url = url + Thread.currentThread().getName().replace("-", "");
        System.out.println("CONTEXTO_URL:" + url);
        basic.setUrl(url);

        AnnotationSessionFactoryBean an = (AnnotationSessionFactoryBean) applicationContext.getBean("&sessionFactory");
        Configuration cfg = an.getConfiguration();
        System.out.println("CONTEXTO:" + cfg.getProperty(Environment.URL));
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }
}
