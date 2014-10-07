package org.maxur.spe.infrastructure;

import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;
import org.maxur.spe.domain.Factory;
import org.maxur.spe.domain.Mail;
import org.maxur.spe.domain.Repository;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class MailRepositoryJDBCImpl implements Repository<Mail> {

    private static Logger LOGGER = getLogger(MailRepositoryJDBCImpl.class);

    private final Factory<Connection> factory;

    private final StopWatchFactory stopWatchFactory;

    public MailRepositoryJDBCImpl(Factory<Connection> factory) {
        this.stopWatchFactory = StopWatchFactory.getInstance("loggingFactory");
        this.factory = factory;
    }

    private static final String SELECT_MAIL_BY_ID = "select ID, ADDRESS, SUBJECT, BODY from MAIL where ID = ?";

    @Override
    public Mail findById(Long id) {
        try (
                Connection con = factory.get();
                PreparedStatement stmt = con.prepareStatement(SELECT_MAIL_BY_ID)
        ) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return assemble(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            LOGGER.error("Don't get database connection", e);
            throw new IllegalStateException("Don't get database connection");
        }
    }

    private static final String SELECT_ALL_MAIL = "select ID, ADDRESS, SUBJECT, BODY from MAIL";

    @Override
    public List<Mail> findAll() {
        try (
                Connection con = factory.get();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_ALL_MAIL)
        ) {
            List<Mail> result = new ArrayList<>();
            while (rs.next()) {
                result.add(assemble(rs));
            }
            return result;
        } catch (SQLException e) {
            LOGGER.error("Don't get database connection", e);
            throw new IllegalStateException("Don't get database connection");
        }
    }

    private Mail assemble(ResultSet rs) throws SQLException {
        return Mail.builder()
                .id(rs.getLong("ID"))
                .toAddress(rs.getString("ADDRESS"))
                .subject(rs.getString("SUBJECT"))
                .body(rs.getString("BODY"))
                .build();
    }


    private static final String INSERT_MAIL = "insert into MAIL " +
            "(ID, ADDRESS, SUBJECT, BODY) values (?, ?, ?, ?)";

    @Override
    public void save(Mail value) {
        StopWatch sw = stopWatchFactory.getStopWatch();
        try (
                Connection con = factory.get();
                PreparedStatement stmt = con.prepareStatement(INSERT_MAIL)
        ) {
            stmt.setLong(1, value.getId());
            stmt.setString(2, value.getToAddress());
            stmt.setString(3, value.getSubject());
            stmt.setString(4, value.getBody());
            stmt.executeUpdate();
            sw.stop("save");
        } catch (SQLException e) {
            sw.stop("save:failure");
            LOGGER.error("Don't get database connection", e);
            throw new IllegalStateException("Don't get database connection");
        }

    }


}
