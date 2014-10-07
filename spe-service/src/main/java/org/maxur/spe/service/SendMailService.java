package org.maxur.spe.service;

import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;
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

import java.sql.Connection;

/**
 * @author Maxim
 * @version 1.0
 * @since <pre>05.10.2014</pre>
 */
public class SendMailService {

    public static final String FROM_ADDRESS = "sender@here.com";

    private StopWatchFactory stopWatchFactory;
    private Worker worker;

    public void init() {
        stopWatchFactory = StopWatchFactory.getInstance("loggingFactory");
        Factory<Connection> factory = new ConnectionFactoryJDBCImpl();
        MailService mailService = new MailServiceJavaxImpl(FROM_ADDRESS);
        Repository<Mail> repository = new MailRepositoryJDBCImpl(factory);
        MailIdService idService = new MailIdServiceJDBCImpl(factory);
        worker = new Worker(mailService, repository, idService);
    }

    public String send(String message) {
        StopWatch sw = stopWatchFactory.getStopWatch();

        final String result = worker.run(message);
        sw.stop("service");
        return result;
    }

    public void done() {
        worker.done();
    }

}
