/*
    SpecRunner - Acceptance Test Driven Development Tool
    Copyright (C) 2011-2012  Thiago Santos

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

    /**
     * Example of data source.
     */
    private final SimpleDataSource source = new SimpleDataSource("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:TESTE_PROVIDER", "sa", "");

    @Override
    public DataSource getDataSource() {
        return source;
    }

    @Override
    public void release() {
        source.release();
    }

    @Override
    public String toString() {
        return "DataSourceProviderImpl->" + source.toString();
    }
}
