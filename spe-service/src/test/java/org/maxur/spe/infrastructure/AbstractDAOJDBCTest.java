package org.maxur.spe.infrastructure;

import org.junit.BeforeClass;
import org.maxur.spe.domain.Factory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>2/28/14</pre>
 */
public class AbstractDAOJDBCTest implements Factory<DataSource> {

    private static Factory<DataSource> dataSourceFactory;

    @BeforeClass
    public static void initTestFixture() throws Exception {
        dataSourceFactory = new DataSourceFactoryEmbeddedDerbyImpl("./temp/database/test01", "", "");
    }

    protected static Connection getConnection() {
        try {
            return dataSourceFactory.get().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataSource get() {
        return dataSourceFactory.get();
    }

}