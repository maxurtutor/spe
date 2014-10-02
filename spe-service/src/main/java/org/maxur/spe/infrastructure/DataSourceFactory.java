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

    public static final String USERNAME = "";

    public static final String PASSWORD = "";

    public static final String DB_PATH = "/persistence/db";

    @Override
    public DataSource get() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        dataSource.setUrl(format("jdbc:derby:%s;create=true", DB_PATH));
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        if (!new File(DB_PATH).exists()) {
            final InputStream ddl = DataSourceFactory.class.getResourceAsStream("/sql/schema.ddl");
            if (ddl == null) {
                LOGGER.error("/sql/schema.ddl not found");
                return null;
            }
            try ( Connection connection = dataSource.getConnection()){

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

        return dataSource;
    }
}
