package example.sql.negative;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.runner.RunWith;
import org.specrunner.SRServices;
import org.specrunner.features.IFeatureManager;
import org.specrunner.junit.BeforeScenario;
import org.specrunner.junit.SRRunnerSpringScenario;
import org.specrunner.junit.SRScenarioListeners;
import org.specrunner.sql.IDataSourceProvider;
import org.specrunner.sql.impl.SimpleDataSource;
import org.specrunner.sql.negative.DatabaseScenarioListener;
import org.specrunner.sql.negative.PluginDbms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SRRunnerSpringScenario.class)
@SRScenarioListeners({ DatabaseScenarioListener.class })
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ApplicationTests {

    @Autowired
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean;

    @Autowired
    DataSource systemSource;
    // new database
    DataSource referenceSource = new SimpleDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:REFERENCE", "sa", "");

    @BeforeScenario
    public void prepare() throws Exception {
        System.out.println("\n=========== SCHEMA =============\n");
        Field exp = SessionFactoryImpl.class.getDeclaredField("schemaExport");
        exp.setAccessible(true);
        SchemaExport export = (SchemaExport) exp.get(((EntityManagerFactoryImpl) entityManagerFactoryBean.getNativeEntityManagerFactory()).getSessionFactory());
        export.setOutputFile("src/test/resources/sgbd_hibernate.sql");
        export.setDelimiter(";");
        export.create(true, false);

        System.out.println("\n=========== MERGE =============\n");
        List<Path> inputs = Arrays.asList(Paths.get("src/test/resources/sgbd_manual.sql"), Paths.get("src/test/resources/sgbd_hibernate.sql"));
        Path output = Paths.get("src/test/resources/sgbd_final.sql");
        int index = 0;
        for (Path path : inputs) {
            byte[] lines = Files.readAllBytes(path);
            if (index++ == 0) {
                Files.write(output, lines);
            } else {
                Files.write(output, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
        }
        Files.copy(Paths.get("src/test/resources/sgbd_final.sql"), Paths.get("target/test-classes/sgbd_final.sql"), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("\n=========== DATASOURCES =============\n");
        IFeatureManager fm = SRServices.getFeatureManager();
        fm.add(PluginDbms.FEATURE_SYSTEM_PROVIDER_INSTANCE, new IDataSourceProvider() {

            @Override
            public void release() {
            }

            @Override
            public DataSource getDataSource() {
                return systemSource;
            }
        });
        fm.add(PluginDbms.FEATURE_REFERENCE_PROVIDER_INSTANCE, new IDataSourceProvider() {

            @Override
            public void release() {
            }

            @Override
            public DataSource getDataSource() {
                return referenceSource;
            }
        });
    }
}