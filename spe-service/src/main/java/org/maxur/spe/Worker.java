package org.maxur.spe;

import org.maxur.spe.domain.Mail;
import org.maxur.spe.domain.MailIDService;
import org.maxur.spe.domain.MailService;
import org.maxur.spe.domain.Repository;
import org.maxur.spe.infrastructure.DataSourceFactory;
import org.maxur.spe.infrastructure.MailIDServiceJDBCImpl;
import org.maxur.spe.infrastructure.MailRepositoryJDBCImpl;
import org.maxur.spe.infrastructure.MailServiceJavaxImpl;

import javax.sql.DataSource;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Worker {

    public static final String FROM_ADDRESS = "sender@here.com";

    public static final String TO_ADDRESS = "receiver@there.com";

    private long count = 0;

    private MailService mailService;

    private Repository<Mail> repository;

    public Worker() {
        init();
    }

    public void init() {
        mailService = new MailServiceJavaxImpl(FROM_ADDRESS);
        final DataSource dataSource = new DataSourceFactory().get();
        repository = new MailRepositoryJDBCImpl(dataSource);
        MailIDService service = new MailIDServiceJDBCImpl(dataSource);
        count = service.getId();
    }

    public String run(String request) throws Exception {
        final String message = format("%d: %s", count++, request);


        final Mail mail = Mail.builder()
                .id(count)
                .subject(message)
                .body(message)
                .toAddress(TO_ADDRESS)
                .build();

        repository.save(mail);

        mailService.send(mail);

        return message;
    }

    public void done() {
        mailService.done();
        repository.done();
    }

}
