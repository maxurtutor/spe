package org.maxur.spe.infrastructure;

import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;
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

    private final StopWatchFactory stopWatchFactory;

    private final Factory<Connection> factory;

    private Long count;

    public MailIdServiceJDBCImpl(Factory<Connection> factory) {
        this.stopWatchFactory = StopWatchFactory.getInstance("loggingFactory");
        this.factory = factory;
    }

    @Override
    public synchronized Long getId() {
        if (count == null) {
            count = loadId();
        }
        return ++count;
    }

    public Long loadId() {
        StopWatch sw = stopWatchFactory.getStopWatch();
        try (
                Connection con = factory.get();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select max(ID) from MAIL")
        ) {
            sw.stop("getid");
            return rs.next() ?
                    rs.getLong(1) :
                    0l;
        } catch (SQLException e) {
            sw.stop("getid:failure");
            LOGGER.error("Don't get database connection", e);
            throw new IllegalStateException("Don't get database connection", e);
        }
    }

}
