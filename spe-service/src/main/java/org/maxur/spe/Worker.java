package org.maxur.spe;

import org.maxur.spe.domain.Mail;
import org.maxur.spe.domain.MailIDService;
import org.maxur.spe.domain.MailService;
import org.maxur.spe.domain.Repository;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Worker {

    public static final String TO_ADDRESS = "receiver@there.com";

    private MailService mailService;

    private Repository<Mail> repository;

    private MailIDService idService;

    public Worker(MailService mailService, Repository<Mail> repository, MailIDService idService) {
        this.mailService = mailService;
        this.repository = repository;
        this.idService = idService;
    }

    public String run(String request) throws Exception {
        final Long id = idService.getId();
        final String message = format("%d: %s", id, request);
        final Mail mail = Mail.builder()
                .id(id)
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
