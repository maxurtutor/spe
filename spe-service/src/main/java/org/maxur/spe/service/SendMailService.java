package org.maxur.spe.service;

import org.maxur.spe.domain.Factory;
import org.maxur.spe.domain.Mail;
import org.maxur.spe.domain.MailIdService;
import org.maxur.spe.domain.MailService;
import org.maxur.spe.domain.Repository;
import org.maxur.spe.domain.Worker;
import org.maxur.spe.infrastructure.ConnectionFactoryJDBCImpl;
import org.maxur.spe.infrastructure.MailIdServiceJDBCImpl;
import org.maxur.spe.infrastructure.MailRepositoryJDBCImpl;
import org.maxur.spe.infrastructure.MailServiceJavaxImpl;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

import static org.maxur.spe.service.ConnectionWrapper.wrap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim
 * @version 1.0
 * @since <pre>05.10.2014</pre>
 */
public class SendMailService {

    private static Logger LOGGER = getLogger(SendMailService.class);

    public static final String FROM_ADDRESS = "sender@here.com";

    private Factory<Connection> factory;

    public void init() {
        factory = new ConnectionFactoryJDBCImpl();
    }

    public String send(String message) {
        Connection connection = startTransaction();
        try {
            MailService mailService = new MailServiceJavaxImpl(FROM_ADDRESS);
            Repository<Mail> repository = new MailRepositoryJDBCImpl(() -> wrap(connection));
            MailIdService idService = new MailIdServiceJDBCImpl(() -> wrap(connection));
            Worker worker = new Worker(mailService, repository, idService);
            String result = worker.run(message);
            commit(connection);
            return result;
        } finally {
            endTransaction(connection);
        }
    }

    private void endTransaction(Connection connection) {
        try {
            connection.setAutoCommit(true);
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Connection is not closed", e);
            throw new IllegalStateException("Connection is not closed", e);
        }
    }

    private void commit(Connection connection) {
        try {
            connection.commit();
        } catch (SQLException e) {
            rollback(connection);
            throw new IllegalStateException("Transaction is not committed", e);
        }
    }

    private void rollback(Connection connection) {
        try {
            System.err.print("Transaction is being rolled back");
            connection.rollback();
        } catch(SQLException e) {
            LOGGER.error("Transaction is not rolled back", e);
            throw new IllegalStateException("Transaction is not rolled back", e);
        }
    }

    private Connection startTransaction() {
        try {
            Connection connection = factory.get();
            // connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE); Deadlock !
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            LOGGER.error("Transaction is not started", e);
            throw new IllegalStateException("Transaction is not started", e);
        }
    }

}
