package org.maxur.spe.infrastructure;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.derby.tools.ij;
import org.maxur.spe.domain.Factory;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim
 * @version 1.0
 * @since <pre>09.10.2014</pre>
 */
public class ConnectionFactoryAtomikosImpl implements Factory<Connection> {

    public static final int POOL_SIZE = 5;

    private static Logger LOGGER = getLogger(ConnectionFactoryAtomikosImpl.class);

    private final DataSource dataSource;

    public static Factory<Connection> make() {
        AtomikosDataSourceBean ds = makeDataSource();
        ConnectionFactoryAtomikosImpl result = new ConnectionFactoryAtomikosImpl(ds);
        if (!DatabaseHelper.isDatabaseExist(result)) {
            DatabaseHelper.makeDatabase(result);
        }
        return result;
    }

    public static AtomikosDataSourceBean makeDataSource() {
        Properties prop = loadProperties();
        Properties p = new Properties();
        p.put("user", prop.getProperty("jdbc.username"));
        p.put("password", prop.getProperty("jdbc.password"));
        p.put("databaseName", prop.getProperty("jdbc.path"));
        p.put("createDatabase", "create");

        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("derby");
        ds.setXaDataSourceClassName("org.apache.derby.jdbc.EmbeddedXADataSource");

        ds.setXaProperties(p);
        ds.setPoolSize(POOL_SIZE);
        return ds;
    }

    private static Properties loadProperties() {
        Properties prop = new Properties();
        InputStream in = ConnectionFactoryAtomikosImpl.class.getResourceAsStream("/jdbc.properties");
        try {
            prop.load(in);
        } catch (IOException e) {
            LOGGER.error("JDBC property file is not loaded", e);
            throw new IllegalStateException("JDBC property file is not loaded", e);
        }
        return prop;
    }

    public ConnectionFactoryAtomikosImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Connection get() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            LOGGER.error("Connection is not created", e);
            throw new IllegalStateException("Connection is not created", e);
        }
    }

}
