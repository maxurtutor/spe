package org.maxur.spe.domain;

import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;

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
        StopWatch stopWatch1 = new LoggingStopWatch("worker");
        final Long id = idService.getId();
        final String message = format("%d: %s", id, request);
        final Mail mail = Mail.builder()
                .id(id)
                .subject(message)
                .body(message)
                .toAddress(TO_ADDRESS)
                .build();
        StopWatch stopWatch2 = new LoggingStopWatch("save");
        repository.save(mail);
        stopWatch2.stop();
        StopWatch stopWatch3 = new LoggingStopWatch("send");
        stopWatch3.stop();
        mailService.send(mail);
        stopWatch1.stop();
        return message;
    }


}
