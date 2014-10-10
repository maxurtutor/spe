package org.maxur.spe.service;

import com.atomikos.icatch.jta.UserTransactionManager;
import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;
import org.maxur.spe.domain.Factory;
import org.maxur.spe.domain.Mail;
import org.maxur.spe.domain.MailIdService;
import org.maxur.spe.domain.MailService;
import org.maxur.spe.domain.Repository;
import org.maxur.spe.domain.Worker;
import org.maxur.spe.infrastructure.ConnectionFactoryAtomikosImpl;
import org.maxur.spe.infrastructure.MailIdServiceJDBCImpl;
import org.maxur.spe.infrastructure.MailRepositoryJDBCImpl;
import org.maxur.spe.infrastructure.MailServiceJavaxImpl;
import org.slf4j.Logger;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import java.sql.Connection;

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

    private StopWatchFactory stopWatchFactory;

    private UserTransactionManager utm;

    public void init() {
        stopWatchFactory = StopWatchFactory.getInstance("loggingFactory");
        factory = ConnectionFactoryAtomikosImpl.make();
        utm = new UserTransactionManager();
        try {
            utm.init();
        } catch (SystemException e) {
            LOGGER.error("Cannot create transaction", e);
            throw new IllegalStateException("Cannot create transaction", e);
        }
    }

    // TODO Lock !
    public synchronized String send(String message) {
        StopWatch sw = stopWatchFactory.getStopWatch();
        MailService mailService = new MailServiceJavaxImpl(FROM_ADDRESS);
        Repository<Mail> repository = new MailRepositoryJDBCImpl(factory);
        MailIdService idService = new MailIdServiceJDBCImpl(factory);
        Worker worker;
        try {
            utm.begin();
            try {
                worker = new Worker(mailService, repository, idService);
                utm.commit();
            } catch (IllegalStateException e) {
                utm.rollback();
                throw new IllegalStateException(e);
            }
        } catch (NotSupportedException | SystemException | RollbackException |
                HeuristicMixedException | HeuristicRollbackException e) {
            LOGGER.error("Cannot execute transaction", e);
            throw new IllegalStateException(e);
        }

        final String result = worker.run(message);
        sw.stop("service");
        return result;
    }


    public void done() {
        utm.close();
    }
}
