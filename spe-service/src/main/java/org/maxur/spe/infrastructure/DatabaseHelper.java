package org.maxur.spe.infrastructure;

import org.apache.derby.tools.ij;
import org.maxur.spe.domain.Factory;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim
 * @version 1.0
 * @since <pre>10.10.2014</pre>
 */
public class DatabaseHelper {

    private static Logger LOGGER = getLogger(DatabaseHelper.class);

    private static final String TEST_SQL = "select count(*) from MAIL";

    public static boolean isDatabaseExist(Factory<Connection> factory) {
        try (
                Connection con = factory.get();
                Statement stmt = con.createStatement()
        ) {
            stmt.executeQuery(TEST_SQL);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }


    public static void makeDatabase(Factory<Connection> factory) {
        final InputStream ddl = ConnectionFactoryJDBCImpl.class.getResourceAsStream("/sql/schema.ddl");
        if (ddl == null) {
            LOGGER.error("DDL schema is not found");
            throw new IllegalStateException("DDL schema is not found");
        }
        try (
                Connection connection = factory.get();
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
