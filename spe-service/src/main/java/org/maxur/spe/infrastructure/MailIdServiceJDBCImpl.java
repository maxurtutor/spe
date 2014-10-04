package org.maxur.spe.infrastructure;

import org.maxur.spe.domain.Factory;
import org.maxur.spe.domain.MailIdService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>10/2/2014</pre>
 */
public class MailIdServiceJDBCImpl implements MailIdService {


    private Factory<Connection> factory;

    public MailIdServiceJDBCImpl(Factory<Connection> factory) {
        this.factory = factory;
    }

    @Override
    public Long getId() {
        try (
                Connection con = factory.get();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select max(ID) from MAIL");
        ) {
            return rs.next() ?
                    rs.getLong(1) + 1l :
                    0l;
        } catch (SQLException e) {
            throw new IllegalStateException("Don't get database connection", e);
        }
    }

}
