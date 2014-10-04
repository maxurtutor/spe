package org.maxur.spe.domain;

import org.slf4j.Logger;

import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Worker {

    private static Logger LOGGER = getLogger(Worker.class);

    public static final String TO_ADDRESS = "receiver@there.com";

    private MailService mailService;

    private Repository<Mail> repository;

    private MailIdService idService;

    public Worker(MailService mailService, Repository<Mail> repository, MailIdService idService) {
        this.mailService = mailService;
        this.repository = repository;
        this.idService = idService;
    }

    public String run(String request) throws Exception {
        LOGGER.debug("run");
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
