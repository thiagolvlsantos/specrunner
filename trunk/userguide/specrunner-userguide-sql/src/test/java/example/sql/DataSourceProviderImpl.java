package example.sql;

import javax.sql.DataSource;

import org.specrunner.sql.IDataSourceProvider;
import org.specrunner.sql.impl.SimpleDataSource;

/**
 * Implementation of a data source provider. Notice that in our application, if
 * you use Spring, for example, you can write the data source provider to
 * recover the context data source.
 * 
 * @author Thiago Santos
 * 
 */
public class DataSourceProviderImpl implements IDataSourceProvider {

    private SimpleDataSource source = new SimpleDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:TESTE_PROVIDER", "sa", "");

    public DataSource getDataSource() {
        return source;
    }

    public void release() {
        source.release();
    }

    @Override
    public String toString() {
        return "DataSourceProviderImpl->" + source.toString();
    }
}
