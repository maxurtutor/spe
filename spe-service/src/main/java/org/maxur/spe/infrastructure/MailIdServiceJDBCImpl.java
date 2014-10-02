package org.maxur.spe.infrastructure;

import org.maxur.spe.domain.Factory;
import org.maxur.spe.domain.MailIdService;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>10/2/2014</pre>
 */
public class MailIdServiceJDBCImpl implements MailIdService {

    private static Logger LOGGER = getLogger(MailIdServiceJDBCImpl.class);

    private Factory<Connection> factory;

    public MailIdServiceJDBCImpl(Factory<Connection> factory) {
        this.factory = factory;
    }

    @Override
    public Long getId() {
        try (
                Connection con = factory.get();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select max(ID) from MAIL")
        ) {
            return rs.next() ?
                    rs.getLong(1) + 1l :
                    0l;
        } catch (SQLException e) {
            LOGGER.error("Don't get database connection", e);
            throw new IllegalStateException("Don't get database connection", e);
        }
    }

}
