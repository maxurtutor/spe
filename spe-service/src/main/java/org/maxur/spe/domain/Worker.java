package org.maxur.spe.domain;

import com.ecyrd.speed4j.StopWatch;
import com.ecyrd.speed4j.StopWatchFactory;

import static java.lang.String.format;

/**
 * @author Maxim Yunusov
 * @version 1.0 14.09.2014
 */
public class Worker {

    public static final String TO_ADDRESS = "receiver@there.com";

    private final StopWatchFactory stopWatchFactory;

    private final MailService mailService;

    private final Repository<Mail> repository;

    private final MailIdService idService;

    public Worker(MailService mailService, Repository<Mail> repository, MailIdService idService) {
        this.stopWatchFactory = StopWatchFactory.getInstance("loggingFactory");
        this.mailService = mailService;
        this.repository = repository;
        this.idService = idService;
    }

    public String run(String request) {
        StopWatch sw = stopWatchFactory.getStopWatch();
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
        sw.stop("worker");
        return message;
    }


}
