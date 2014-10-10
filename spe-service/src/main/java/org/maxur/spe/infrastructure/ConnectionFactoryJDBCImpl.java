package org.maxur.spe.infrastructure;

import org.apache.derby.tools.ij;
import org.maxur.spe.domain.Factory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 03.10.2014
 */
public class ConnectionFactoryJDBCImpl implements Factory<Connection> {

    private static Logger LOGGER = getLogger(ConnectionFactoryJDBCImpl.class);

    private final Properties connectionProps;

    private final String url;

    public ConnectionFactoryJDBCImpl() {
        Properties prop = loadProperties();
        connectionProps = new Properties();
        connectionProps.put("user", prop.getProperty("jdbc.username"));
        connectionProps.put("password", prop.getProperty("jdbc.password"));
        url = prop.getProperty("jdbc.url");
        if (!DatabaseHelper.isDatabaseExist(this)) {
            DatabaseHelper.makeDatabase(this);
        }
    }

    private Properties loadProperties() {
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("/jdbc.properties");
        try {
            prop.load(in);
        } catch (IOException e) {
            LOGGER.error("JDBC property file is not loaded", e);
            throw new IllegalStateException("JDBC property file is not loaded", e);
        }
        return prop;
    }

    @Override
    public Connection get() {
        try {
            return DriverManager.getConnection(
                    url,
                    connectionProps);
        } catch (SQLException e) {
            LOGGER.error("Connection is not created", e);
            throw new IllegalStateException("Connection is not created", e);
        }
    }

}
