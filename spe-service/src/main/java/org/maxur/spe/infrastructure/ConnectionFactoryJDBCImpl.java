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

    private static Logger LOGGER = getLogger(MailServiceJavaxImpl.class);

    private final Properties connectionProps;

    private final String url;

    public ConnectionFactoryJDBCImpl() {
        Properties prop = loadProperties();
        connectionProps = new Properties();
        connectionProps.put("user", prop.getProperty("jdbc.username"));
        connectionProps.put("password", prop.getProperty("jdbc.password"));
        url = prop.getProperty("jdbc.url");
        if (!isDatabaseExist()) {
            makeDatabase();
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

    private static final String TEST_SQL = "select * from MAIL";

    private boolean isDatabaseExist() {
        try (
                Connection con = get();
                Statement stmt = con.createStatement()
        ) {
            stmt.executeQuery(TEST_SQL);
            return true;
        } catch (SQLException e) {
            return false;
        }
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


    private void makeDatabase() {
        final InputStream ddl = ConnectionFactoryJDBCImpl.class.getResourceAsStream("/sql/schema.ddl");
        if (ddl == null) {
            LOGGER.error("DDL schema is not found");
            throw new IllegalStateException("DDL schema is not found");
        }
        try (
                Connection connection = get();
                LogOutputStream stream = new LogOutputStream(LOGGER)
        ){
            ij.runScript(connection,
                    ddl,
                    "UTF-8",
                    stream,
                    "UTF-8"
            );
        } catch (SQLException | UnsupportedEncodingException e) {
            LOGGER.error("Database is not created", e);
            throw new IllegalStateException("Database is not created", e);
        }
    }

}
