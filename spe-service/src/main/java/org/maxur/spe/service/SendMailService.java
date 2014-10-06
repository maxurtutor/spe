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
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

import java.sql.Connection;

/**
 * @author Maxim
 * @version 1.0
 * @since <pre>05.10.2014</pre>
 */
public class SendMailService {

    public static final String FROM_ADDRESS = "sender@here.com";

    private Worker worker;

    public void init() {
        Factory<Connection> factory = new ConnectionFactoryJDBCImpl();
        MailService mailService = new MailServiceJavaxImpl(FROM_ADDRESS);
        Repository<Mail> repository = new MailRepositoryJDBCImpl(factory);
        MailIdService idService = new MailIdServiceJDBCImpl(factory);
        worker = new Worker(mailService, repository, idService);
    }

    public String send(String message) {
        StopWatch stopWatch1 = new Slf4JStopWatch("service");
        String run = worker.run(message);
        stopWatch1.stop();
        return run;
    }


}
