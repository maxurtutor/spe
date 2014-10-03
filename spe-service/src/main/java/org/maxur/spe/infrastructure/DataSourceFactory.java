package org.maxur.spe.infrastructure;

import org.apache.derby.tools.ij;
import org.maxur.spe.domain.Factory;
import org.slf4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 03.10.2014
 */
public class DataSourceFactory implements Factory<DataSource> {

    private static Logger LOGGER = getLogger(MailServiceJavaxImpl.class);

    public final String username;

    public final String password;

    public final String dbPath;

    private DataSource dataSource;

    public DataSourceFactory(String dbPath, String password, String username) {
        this.dbPath = dbPath;
        this.password = password;
        this.username = username;
    }

    @Override
    public DataSource get() {
        if (dataSource == null) {
            dataSource = makeDataSource();
            if (!new File(dbPath).exists()) {
                makeDatabase(dataSource);
            }
        }
        return dataSource;
    }

    private DataSource makeDataSource() {
        final DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        ds.setUrl(format("jdbc:derby:%s;create=true", dbPath));
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    private void makeDatabase(DataSource ds) {
        final InputStream ddl = DataSourceFactory.class.getResourceAsStream("/sql/schema.ddl");
        if (ddl == null) {
            LOGGER.error("DDL schema is not found");
            throw new IllegalStateException("DDL schema is not found");
        }
        try ( Connection connection = ds.getConnection()){
            ij.runScript(connection,
                    ddl,
                    "UTF-8",
                    System.out,
                    "UTF-8"
            );
        } catch (SQLException | UnsupportedEncodingException e) {
            LOGGER.error("Database is not created", e);
        }
    }
}
