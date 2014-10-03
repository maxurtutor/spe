package org.maxur.spe;

import org.apache.derby.tools.ij;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.maxur.spe.domain.Factory;
import org.maxur.spe.infrastructure.LogOutputStream;
import org.slf4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

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

    private static Logger LOGGER = getLogger(AbstractDAOJDBCTest.class);

    protected static DriverManagerDataSource dataSource;

    @BeforeClass
    public static void initTestFixture() throws Exception {
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        dataSource.setUrl("jdbc:derby:c:\\temp\\database\\test01;create=true");
        dataSource.setUsername("");
        dataSource.setPassword("");

        try(LogOutputStream stream = new LogOutputStream(LOGGER)) {
            ij.runScript(getConnection(),
                    AbstractDAOJDBCTest.class.getResourceAsStream("/sql/schema.ddl"),
                    "UTF-8",
                    stream,
                    "UTF-8"
            );
        }
    }

    protected static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cleans up the session.
     */
    @AfterClass
    public static void closeTestFixture() {
    }


    @Override
    public DataSource get() {
        return dataSource;
    }

}