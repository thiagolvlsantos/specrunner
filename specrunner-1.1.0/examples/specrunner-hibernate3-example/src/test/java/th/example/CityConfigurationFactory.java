package th.example;

import org.hibernate.cfg.Configuration;

public class CityConfigurationFactory {

    public static Configuration getConf() throws Exception {
        return new CityConfiguration().getConfiguration();
    }
}