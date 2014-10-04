package org.maxur.spe.client;

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
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Client {


    public static final String FROM_ADDRESS = "sender@here.com";

    private Worker worker;

    public static void main(String[] args) throws Exception {
        final Client client = new Client();
        client.init();
        client.run();
        client.done();
    }

    private void init() {
        MailService mailService = new MailServiceJavaxImpl(FROM_ADDRESS);
        Factory<Connection> factory = new ConnectionFactoryJDBCImpl();
        Repository<Mail> repository = new MailRepositoryJDBCImpl(factory);
        MailIdService idService = new MailIdServiceJDBCImpl(factory);
        worker = new Worker(mailService, repository, idService);
    }

    private void run() throws Exception {
        for (int i = 0; i < 20000; i++) {
            String response = worker.run(makeRequest());
            System.out.println(response);
        }
    }

    private String makeRequest() {
        return "Hello";
    }

    private void done() {
        worker.done();
    }


}
