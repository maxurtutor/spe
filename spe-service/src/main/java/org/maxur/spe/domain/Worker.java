package org.maxur.spe.domain;

import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Worker {

    public static final String TO_ADDRESS = "receiver@there.com";

    private MailService mailService;

    private Repository<Mail> repository;

    private MailIdService idService;

    public Worker(MailService mailService, Repository<Mail> repository, MailIdService idService) {
        this.mailService = mailService;
        this.repository = repository;
        this.idService = idService;
    }

    public String run(String request) {
        StopWatch sw1 = new Slf4JStopWatch("work");
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
        sw1.stop();
        return message;
    }


}
